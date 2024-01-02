/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.generator.ValueSpec;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.generator.specs.BigIntegerSpec;
import org.instancio.generator.specs.BooleanSpec;
import org.instancio.generator.specs.ByteSpec;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.generator.specs.DoubleSpec;
import org.instancio.generator.specs.FileSpec;
import org.instancio.generator.specs.FloatSpec;
import org.instancio.generator.specs.InstantSpec;
import org.instancio.generator.specs.IntegerSpec;
import org.instancio.generator.specs.LocalDateSpec;
import org.instancio.generator.specs.LocalDateTimeSpec;
import org.instancio.generator.specs.LocalTimeSpec;
import org.instancio.generator.specs.LongSpec;
import org.instancio.generator.specs.LoremIpsumSpec;
import org.instancio.generator.specs.Mod10Spec;
import org.instancio.generator.specs.Mod11Spec;
import org.instancio.generator.specs.OffsetDateTimeSpec;
import org.instancio.generator.specs.OffsetTimeSpec;
import org.instancio.generator.specs.PathSpec;
import org.instancio.generator.specs.ShortSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.generator.specs.StringSpec;
import org.instancio.generator.specs.URISpec;
import org.instancio.generator.specs.URLSpec;
import org.instancio.generator.specs.UUIDStringSpec;
import org.instancio.generator.specs.YearMonthSpec;
import org.instancio.generator.specs.YearSpec;
import org.instancio.generator.specs.ZonedDateTimeSpec;
import org.instancio.internal.util.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.fail;

/**
 * Interface that inherit from {@link ValueSpec} should override
 * methods inherited from parent interfaces. For example,
 * {@link StringSpec} should override methods from {@link StringGeneratorSpec}
 * in order to override the return type from {@code StringGeneratorSpec}
 * to {@code StringSpec}.
 *
 * <p>This ensures that when the Value Spec API is used, methods from all
 * interfaces can be chained together, for example:
 *
 * <pre>
 *   Gen.string().prefix("foo").get();
 * </pre>
 *
 * <p>where {@code prefix()} is from {@code StringGeneratorSpec} and {@code get()}
 * is from {@code ValueSpec}.
 */
class ValueSpecSubtypesOverrideMethodsTest {

    @Test
    void booleanSpec() {
        assertSpecOverridesSuperMethods(BooleanSpec.class);
    }

    @Test
    void characterSpec() {
        assertSpecOverridesSuperMethods(CharacterSpec.class);
    }

    @ValueSource(classes = {
            BigDecimalSpec.class,
            BigIntegerSpec.class,
            ByteSpec.class,
            DoubleSpec.class,
            FloatSpec.class,
            IntegerSpec.class,
            LongSpec.class,
            ShortSpec.class
    })
    @ParameterizedTest
    void numericSpec(final Class<?> specClass) {
        assertSpecOverridesSuperMethods(specClass);
    }

    @ValueSource(classes = {
            PathSpec.class,
            FileSpec.class
    })
    @ParameterizedTest
    void pathSpec(final Class<?> specClass) {
        assertSpecOverridesSuperMethods(specClass, "createDirectory", "createFile");
    }

    @Test
    void stringSpec() {
        assertSpecOverridesSuperMethods(StringSpec.class);
    }

    @ValueSource(classes = {
            InstantSpec.class,
            LocalDateSpec.class,
            LocalDateTimeSpec.class,
            LocalTimeSpec.class,
            OffsetDateTimeSpec.class,
            OffsetTimeSpec.class,
            YearMonthSpec.class,
            YearSpec.class,
            ZonedDateTimeSpec.class

    })
    @ParameterizedTest
    void temporalSpec(final Class<?> specClass) {
        assertSpecOverridesSuperMethods(specClass);
    }

    @ValueSource(classes = {
            LoremIpsumSpec.class,
            UUIDStringSpec.class
    })
    @ParameterizedTest
    void text(final Class<?> specClass) {
        assertSpecOverridesSuperMethods(specClass);
    }

    @ValueSource(classes = {
            URISpec.class,
            URLSpec.class
    })
    @ParameterizedTest
    void net(final Class<?> specClass) {
        assertSpecOverridesSuperMethods(specClass);
    }

    @ValueSource(classes = {
            Mod10Spec.class,
            Mod11Spec.class
    })
    @ParameterizedTest
    void checksum(final Class<?> specClass) {
        assertSpecOverridesSuperMethods(specClass);
    }

    /**
     * Collects methods from super interfaces of {@code specClass}
     * and checks that the class overrides all the inherited methods,
     * excepts the {@code excludedMethods}.
     */
    private static void assertSpecOverridesSuperMethods(final Class<?> specClass, final String... excludedMethods) {
        final List<Method> superMethods = getMethodsFromParentInterfaces(
                specClass, CollectionUtils.asSet(excludedMethods));

        assertSpecOverridesAll(specClass, superMethods);
    }

    /**
     * Returns all the parent interface methods that the {@code specClass}
     * should override.
     *
     * @param specClass       the spec class under test
     * @param excludedMethods these are terminal methods, therefore they should
     *                        have ValueSpec as the return type to prevent
     *                        additional method calls
     * @return all methods that the {@code specClass} should override
     * in order to return itself.
     */
    private static List<Method> getMethodsFromParentInterfaces(
            final Class<?> specClass,
            final Set<String> excludedMethods) {

        // These are terminal methods from ValueSpec
        // They return a result and don't need to be overridden
        // We're only interested in builder methods used for chaining API calls
        final Set<String> exclusions = new HashSet<>(
                CollectionUtils.asSet("get", "list", "map", "stream", "toModel"));

        exclusions.addAll(excludedMethods);

        final List<Method> methods = new ArrayList<>();
        for (Class<?> interfaceClass : specClass.getInterfaces()) {
            final List<Method> filtered = Arrays.stream(interfaceClass.getDeclaredMethods())
                    .filter(m -> !exclusions.contains(m.getName()) && !m.getName().contains("jacoco"))
                    .collect(Collectors.toList());

            methods.addAll(filtered);
        }
        return methods;
    }

    private static void assertSpecOverridesAll(final Class<?> specClass, final List<Method> methods) {
        for (Method method : methods) {
            if (!overrides(specClass, method)) {
                fail("%s is not overridden by %s", method, specClass);
            }
        }
    }

    private static boolean overrides(final Class<?> specClass, final Method superMethod) {
        return Arrays.stream(specClass.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals(superMethod.getName())
                        // Not comparing parameter types because some superclass method parameters
                        // are generic, and do not match subclass parameter types, e.g.:
                        // - NumberGeneratorSpec<T extends Number> vs IntegerSpec: Number vs Integer
                        // - TemporalGeneratorSpec<T> vs InstantSpec: Object vs Instant
                        //
                        // Method name + parameter count is good enough for this test.
                        && superMethod.getParameterCount() == m.getParameterCount()
                        && m.getReturnType() == specClass);
    }
}
