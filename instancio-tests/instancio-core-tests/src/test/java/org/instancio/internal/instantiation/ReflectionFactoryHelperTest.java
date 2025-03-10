/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.instantiation;

import org.instancio.test.support.pojo.misc.WithNonDefaultConstructorThrowingError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionFactoryHelperTest {

    @Test
    void shouldBypassConstructorInvocation() {
        final WithNonDefaultConstructorThrowingError result = ReflectionFactoryHelper.createInstance(
                WithNonDefaultConstructorThrowingError.class);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnNullIfInstantiationFails() {
        class WithStaticInitializerThrowingError {
            static {
                intentionallyFail();
            }

            private static void intentionallyFail() {
                throw new RuntimeException("Intentionally fail");
            }
        }

        final WithStaticInitializerThrowingError result = ReflectionFactoryHelper.createInstance(
                WithStaticInitializerThrowingError.class);

        assertThat(result).isNull();
    }
}
