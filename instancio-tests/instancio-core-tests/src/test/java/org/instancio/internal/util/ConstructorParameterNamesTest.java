/*
 * Copyright 2022-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.util;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Except for {@link CompilerOptionsTest}, which compiles its own fixture,
 * these tests rely on the test classes being compiled with debug information,
 * which the Maven build enables by default.
 *
 * @see ConstructorParameterNames
 */
class ConstructorParameterNamesTest {

    @SuppressWarnings("unused")
    private static class MultiParam {
        MultiParam(final String first,
                   final long second,
                   final double third,
                   final String fourth,
                   final long[] fifth,
                   final int sixth) {
            // no-op
        }
    }

    @SuppressWarnings("unused")
    private static class MultipleConstructors {
        MultipleConstructors() { /* no-op */ }

        MultipleConstructors(final int number) { /* no-op */ }

        MultipleConstructors(final List<String> values, final boolean flag) { /* no-op */ }
    }

    @SuppressWarnings("unused")
    private record PersonRecord(String name, int age) {}

    @Test
    void multiParamConstructor() throws NoSuchMethodException {
        final Constructor<?> ctor = MultiParam.class.getDeclaredConstructor(
                String.class, long.class, double.class, String.class, long[].class, int.class);

        // long/double occupy two local variable slots; long[] occupies one
        assertThat(ConstructorParameterNames.resolve(ctor))
                .containsExactly("first", "second", "third", "fourth", "fifth", "sixth");
    }

    @Test
    void multipleConstructorsResolvedByDescriptor() throws NoSuchMethodException {
        assertThat(ConstructorParameterNames.resolve(
                MultipleConstructors.class.getDeclaredConstructor()))
                .isEmpty();

        assertThat(ConstructorParameterNames.resolve(
                MultipleConstructors.class.getDeclaredConstructor(int.class)))
                .containsExactly("number");

        assertThat(ConstructorParameterNames.resolve(
                MultipleConstructors.class.getDeclaredConstructor(List.class, boolean.class)))
                .containsExactly("values", "flag");
    }

    @Test
    void recordCanonicalConstructor() throws NoSuchMethodException {
        final Constructor<?> ctor = PersonRecord.class.getDeclaredConstructor(String.class, int.class);

        assertThat(ConstructorParameterNames.resolve(ctor))
                .containsExactly("name", "age");
    }

    @Test
    void jdkClassDoesNotThrow() throws NoSuchMethodException {
        final Constructor<?> ctor = StringBuilder.class.getDeclaredConstructor(String.class);

        // depending on how the JDK was compiled, names may or may not
        // be available; must degrade gracefully either way
        final String[] names = ConstructorParameterNames.resolve(ctor);

        if (names != null) {
            assertThat(names).hasSize(1);
        }
    }

    @Test
    void localVariableTableReaderReturnsAllConstructors() {
        final Map<String, String[]> results =
                LocalVariableTableReader.getConstructorParameterNames(MultipleConstructors.class);

        assertThat(results)
                .containsOnlyKeys("()V", "(I)V", "(Ljava/util/List;Z)V");
    }

    /**
     * The classes used by the other tests are compiled by the build, which
     * emits debug information but not {@code -parameters}. These compile
     * a class on the fly, which allows each source of parameter names to be
     * covered in isolation, including their absence.
     */
    @Nested
    class CompilerOptionsTest {

        private static final String CLASS_NAME = "CompiledPojo";

        private static final String SOURCE = "public class " + CLASS_NAME + " {\n"
                + "    private final String name;\n"
                + "    private final long id;\n"
                + "    public " + CLASS_NAME + "(String name, long id) {\n"
                + "        this.name = name;\n"
                + "        this.id = id;\n"
                + "    }\n"
                + "}\n";

        /**
         * Control: without it, a test asserting that names are unavailable
         * would also pass if the class could not be compiled or loaded at all.
         */
        @Test
        void resolvedFromLocalVariableTable(@TempDir final Path dir) throws Exception {
            assertThat(resolveWith(dir, "-g")).containsExactly("name", "id");
        }

        /**
         * Debug information is suppressed, so the names can only have come
         * from the {@code MethodParameters} attribute.
         */
        @Test
        void resolvedFromReflectionWithParametersOption(@TempDir final Path dir) throws Exception {
            assertThat(resolveWith(dir, "-g:none", "-parameters")).containsExactly("name", "id");
        }

        /**
         * Neither source is present: javac's default omits the {@code LocalVariableTable}.
         */
        @Test
        void unresolvedWithJavacDefaultOptions(@TempDir final Path dir) throws Exception {
            assertThat(resolveWith(dir)).isNull();
        }

        @Test
        void unresolvedWithoutDebugInformation(@TempDir final Path dir) throws Exception {
            assertThat(resolveWith(dir, "-g:none")).isNull();
        }

        private String @Nullable [] resolveWith(final Path dir, final String... options) throws Exception {
            final Path source = dir.resolve(CLASS_NAME + ".java");
            Files.writeString(source, SOURCE);

            final List<String> args = new ArrayList<>(List.of("-d", dir.toString()));
            args.addAll(List.of(options));
            args.add(source.toString());

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            assertThat(compiler.run(null, null, null, args.toArray(new String[0])))
                    .as("Compilation of the test fixture should succeed")
                    .isZero();

            // The class loader must stay open: the reader resolves
            // the class file through it
            try (URLClassLoader classLoader = new URLClassLoader(new URL[]{dir.toUri().toURL()}, null)) {
                final Constructor<?> ctor = classLoader.loadClass(CLASS_NAME)
                        .getDeclaredConstructor(String.class, long.class);

                return ConstructorParameterNames.resolve(ctor);
            }
        }
    }
}
