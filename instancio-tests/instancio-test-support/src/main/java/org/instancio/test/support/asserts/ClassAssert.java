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
package org.instancio.test.support.asserts;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ClassAssert extends AbstractAssert<ClassAssert, Class<?>> {

    private ClassAssert(final Class<?> klass) {
        super(klass, ClassAssert.class);
    }

    public static ClassAssert assertThatClass(final Class<?> actual) {
        return new ClassAssert(actual);
    }

    public static ClassAssert assertThatSuperInterface(final Class<?> klass, final String interfaceSimpleName) {
        final Class<?> superInterface = getSuperInterface(klass, interfaceSimpleName);
        assertThat(superInterface)
                .as("%s (%s)", klass, interfaceSimpleName)
                .isNotNull();
        return new ClassAssert(superInterface);
    }

    public ClassAssert hasNoMethods() {
        assertThat(getMethodNamesFromActual()).isEmpty();
        return this;
    }

    public ClassAssert hasAllMethodsSatisfying(final Predicate<Method> predicate) {
        final SoftAssertions softly = new SoftAssertions();

        Arrays.stream(actual.getMethods())
                .forEach(method -> softly.assertThat(method).matches(predicate));

        softly.assertAll();
        return this;
    }

    public ClassAssert hasNoMethodsNamed(final String... methodNames) {
        assertThat(getMethodNamesFromActual()).doesNotContain(methodNames);
        return this;
    }

    public ClassAssert hasOnlyMethodsNamed(final String... methodNames) {
        assertThat(getMethodNamesFromActual()).containsOnly(methodNames);
        return this;
    }

    public ClassAssert isNotAssignableFromOrTo(final Class<?> klass) {
        assertThat(actual.isAssignableFrom(klass)).isFalse();
        assertThat(klass.isAssignableFrom(actual)).isFalse();

        return this;
    }

    public ClassAssert isNotPublic() {
        assertThat(Modifier.isPublic(actual.getModifiers()))
                .as("%s should not be public", actual.getSimpleName())
                .isFalse();
        return this;
    }

    public MethodsAssert withMethodsMatching(final Predicate<Method> predicate) {
        isNotNull();

        final Method[] methods = Arrays.stream(actual.getDeclaredMethods())
                .filter(predicate)
                .toArray(Method[]::new);

        return MethodsAssert.assertMethods(methods);
    }

    public MethodsAssert withMethodNameMatching(final String methodName) {
        return withMethodsMatching(m -> m.getName().equals(methodName));
    }

    private List<String> getMethodNamesFromActual() {
        return Arrays.stream(actual.getMethods())
                .map(Method::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

    }

    private static Class<?> getSuperInterface(final Class<?> klass, final String interfaceSimpleName) {
        for (Class<?> c : klass.getInterfaces()) {
            if (c.getSimpleName().equals(interfaceSimpleName)) {
                return c;
            }
        }
        return null;
    }
}
