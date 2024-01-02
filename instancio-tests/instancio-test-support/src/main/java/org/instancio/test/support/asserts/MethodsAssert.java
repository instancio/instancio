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

import static org.assertj.core.api.Assertions.assertThat;

public class MethodsAssert extends AbstractAssert<MethodsAssert, Method[]> {

    private MethodsAssert(final Method... methods) {
        super(methods, MethodsAssert.class);
    }

    public static MethodsAssert assertMethods(final Method... actual) {
        return new MethodsAssert(actual);
    }

    public MethodsAssert haveReturnType(final Class<?> returnType) {
        assertThat(actual)
                .isNotEmpty()
                .allSatisfy(method -> assertThat(method.getReturnType())
                        .as("method return type")
                        .isEqualTo(returnType));

        return this;
    }

    public MethodsAssert hasSize(final int expected) {
        assertThat(actual).hasSize(expected);
        return this;
    }
}
