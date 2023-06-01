/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.assigners;

import org.instancio.assignment.MethodModifier;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssignerUtilTest {
    private static final String PUBLIC_METHOD = "publicMethod";
    private static final String PUBLIC_STATIC_METHOD = "publicStaticMethod";
    private static final String PROTECTED_METHOD = "protectedMethod";
    private static final String PACKAGE_PRIVATE_METHOD = "packagePrivateMethod";
    private static final String PACKAGE_PRIVATE_STATIC_METHOD = "packagePrivateStaticMethod";
    private static final String PRIVATE_METHOD = "privateMethod";
    private static final String PRIVATE_STATIC_METHOD = "privateStaticMethod";

    private static final Set<String> ALL_METHODS = Set.of(
            PUBLIC_METHOD,
            PUBLIC_STATIC_METHOD,
            PROTECTED_METHOD,
            PACKAGE_PRIVATE_METHOD,
            PACKAGE_PRIVATE_STATIC_METHOD,
            PRIVATE_METHOD,
            PRIVATE_STATIC_METHOD
    );

    //@formatter:off
    @SuppressWarnings("unused")
    private static class Pojo {
        public void publicMethod() {}
        public static void publicStaticMethod() {}
        protected void protectedMethod() {}
        void packagePrivateMethod() {}
        static void packagePrivateStaticMethod() {}
        private void privateMethod() {}
        private static void privateStaticMethod() {}
    }
    //@formatter:on

    @Test
    void excludeNone() {
        final int exclude = 0;
        final String[] expectedExclusions = {};

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludePublic() {
        final int exclude = MethodModifier.PUBLIC;
        final String[] expectedExclusions = {PUBLIC_METHOD, PUBLIC_STATIC_METHOD};

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludePrivate() {
        final int exclude = MethodModifier.PRIVATE;
        final String[] expectedExclusions = {PRIVATE_METHOD, PRIVATE_STATIC_METHOD};

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludePackagePrivate() {
        final int exclude = MethodModifier.PACKAGE_PRIVATE;
        final String[] expectedExclusions = {PACKAGE_PRIVATE_METHOD, PACKAGE_PRIVATE_STATIC_METHOD};

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludePrivateProtectedAndStatic() {
        final int exclude = MethodModifier.PRIVATE
                | MethodModifier.PROTECTED
                | MethodModifier.STATIC;

        final String[] expectedExclusions = {
                PUBLIC_STATIC_METHOD,
                PACKAGE_PRIVATE_STATIC_METHOD,
                PROTECTED_METHOD,
                PRIVATE_METHOD,
                PRIVATE_STATIC_METHOD
        };

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludePrivateProtectedAndPackagePrivate() {
        final int exclude = MethodModifier.PRIVATE
                | MethodModifier.PROTECTED
                | MethodModifier.PACKAGE_PRIVATE;

        final String[] expectedExclusions = {
                PACKAGE_PRIVATE_METHOD,
                PACKAGE_PRIVATE_STATIC_METHOD,
                PROTECTED_METHOD,
                PRIVATE_METHOD,
                PRIVATE_STATIC_METHOD
        };

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludePrivateProtectedPackagePrivateAndStatic() {
        // effectively allows only non-static public methods
        final int exclude = MethodModifier.PRIVATE
                | MethodModifier.PROTECTED
                | MethodModifier.PACKAGE_PRIVATE
                | MethodModifier.STATIC;

        final String[] expectedExclusions = {
                PUBLIC_STATIC_METHOD,
                PACKAGE_PRIVATE_METHOD,
                PACKAGE_PRIVATE_STATIC_METHOD,
                PROTECTED_METHOD,
                PRIVATE_METHOD,
                PRIVATE_STATIC_METHOD
        };

        assertExcludedMethods(exclude, expectedExclusions);
    }

    @Test
    void excludeStatic() {
        final int exclude = MethodModifier.STATIC;

        final String[] expectedExclusions = {
                PUBLIC_STATIC_METHOD,
                PACKAGE_PRIVATE_STATIC_METHOD,
                PRIVATE_STATIC_METHOD
        };

        assertExcludedMethods(exclude, expectedExclusions);
    }

    private static void assertExcludedMethods(final int modifierExclusions, final String... expectedMethods) {
        assertExcluded(true, modifierExclusions, expectedMethods);

        // remaining methods should NOT be excluded
        final Set<String> included = new HashSet<>(ALL_METHODS);
        Stream.of(expectedMethods).forEach(included::remove);
        assertExcluded(false, modifierExclusions, included.toArray(String[]::new));
    }

    private static void assertExcluded(
            final boolean expectedResult, final int modifierExclusions, final String... expectedMethods) {

        for (String methodName : expectedMethods) {
            final Method method = getMethod(methodName);
            final int modifiers = method.getModifiers();
            final boolean result = AssignerUtil.isExcluded(modifiers, modifierExclusions);

            assertThat(result)
                    .as("Method: '%s'", methodName)
                    .isEqualTo(expectedResult);
        }
    }

    private static Method getMethod(final String method) {
        try {
            return Pojo.class.getDeclaredMethod(method);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Method not found: " + method);
        }
    }
}
