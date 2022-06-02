/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.contract;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for things that should not be allowed using selector APIs.
 * These are to ensure that certain methods are not inadvertently exposed,
 * for example when refactoring.
 */
class SelectorApiContractTest {

    private static final Path SRC_DIR = Paths.get("src", "main", "non-build");
    private static final Path OUT_DIR = Paths.get("target", "non-build");
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(OUT_DIR);
    }

    @Test
    @DisplayName("Cannot pass 'Selector' to 'within()' method")
    void passingSelectorToWithinNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_SelectorWithinSelector.java",
                "method within in interface org.instancio.Selector cannot be applied to given types;",
                "required: ", "org.instancio.Scope[]",
                "found: ", "org.instancio.Selector",
                "reason: ", "varargs mismatch; org.instancio.Selector cannot be converted to org.instancio.Scope");
    }

    @Test
    @DisplayName("Group selector has no 'within()' method")
    void groupSelectorDoesNotHaveAWithinMethod() throws Exception {
        assertCompilationError("NonCompilable_GroupSelectorDoesNotExposeWithinMethod.java",
                "cannot find symbol",
                "symbol: ", "method within(org.instancio.Scope)",
                "location: ", "interface org.instancio.SelectorGroup");
    }

    @Test
    @DisplayName("Passing scope as a selector")
    void passingScopeAsSelector() throws Exception {
        assertCompilationError("NonCompilable_PassingScopeAsSelector.java",
                "argument mismatch; org.instancio.Scope cannot be converted to org.instancio.TargetSelector");
    }

    @Test
    @DisplayName("Passing selector group to within")
    void passingGroupToWithin() throws Exception {
        assertCompilationError("NonCompilable_PassingGroupToWithin.java",
                "varargs mismatch; org.instancio.SelectorGroup cannot be converted to org.instancio.Scope");
    }

    @Test
    @DisplayName("Selector group has no scope() method")
    void selectorGroupHasNoScope() throws Exception {
        assertCompilationError("NonCompilable_SelectorGroupHasNoScope.java",
                "cannot find symbol",
                "symbol: ", "method scope()",
                "location: ", "interface org.instancio.SelectorGroup");
    }

    @Test
    @DisplayName("Selector group has no toScope() method")
    void selectorGroupHasNoToScope() throws Exception {
        assertCompilationError("NonCompilable_SelectorGroupCannotBeConvertedToScope.java",
                "cannot find symbol",
                "symbol: ", "method toScope()",
                "location: ", "interface org.instancio.SelectorGroup");
    }

    @Test
    @DisplayName("Nested groups not allowed")
    void nestedGroups() throws Exception {
        assertCompilationError("NonCompilable_NestedGroups.java",
                "no suitable method found for all(org.instancio.SelectorGroup)");
    }

    @Test
    @DisplayName("Chaining within() methods not allowed")
    void cannotChainWithinMethods() throws Exception {
        assertCompilationError("NonCompilable_CannotChainWithinMethods.java",
                "cannot find symbol",
                "location: ", "interface org.instancio.GroupableSelector");
    }

    private static void assertCompilationError(final String sourceFile, final String... expectedErrorMsg) throws Exception {
        final DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(collector, null, null)) {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(OUT_DIR.toFile()));
            final Path sourcePath = SRC_DIR.resolve(sourceFile);
            final Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
                    Collections.singleton(sourcePath.toString()));
            final Set<String> options = Collections.singleton("-Xdiags:verbose");
            final JavaCompiler.CompilationTask task = COMPILER.getTask(null, fileManager, collector, options,
                    null, compilationUnits);

            assertThat(task.call()).as("Expected a compilation error").isFalse();

            assertThat(collector.getDiagnostics()).hasSize(1).first().satisfies(diagnostic -> {
                assertThat(diagnostic.getKind()).isEqualTo(Diagnostic.Kind.ERROR);
                assertThat(diagnostic.getMessage(Locale.getDefault())).containsSubsequence(expectedErrorMsg);
            });
        }
    }
}
