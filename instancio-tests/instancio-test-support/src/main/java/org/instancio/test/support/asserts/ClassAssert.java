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
package org.instancio.test.support.asserts;

import org.assertj.core.api.AbstractAssert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ClassAssert extends AbstractAssert<ClassAssert, Class<?>> {

    private ClassAssert(final Class<?> klass) {
        super(klass, ClassAssert.class);
    }

    public static ClassAssert assertThatClass(final Class<?> actual) {
        return new ClassAssert(actual);
    }

    public ClassAssert hasNoMethodsNamed(final String... methodNames) {
        assertThat(actual.getMethods())
                .extracting(Method::getName)
                .doesNotContain(methodNames);

        return this;
    }

    public ClassAssert isNotAssignableFromOrTo(final Class<?> klass) {
        assertThat(actual.isAssignableFrom(klass)).isFalse();
        assertThat(klass.isAssignableFrom(actual)).isFalse();

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
}
