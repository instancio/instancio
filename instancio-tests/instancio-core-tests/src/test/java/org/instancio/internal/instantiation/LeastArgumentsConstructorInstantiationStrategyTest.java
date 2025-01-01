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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeastArgumentsConstructorInstantiationStrategyTest {

    private static final String EXPECTED_CONSTRUCTOR = "expected-constructor";

    private final InstantiationStrategy strategy = new LeastArgumentsConstructorInstantiationStrategy();

    @Test
    void instantiate() {
        final WithMultipleConstructors result = strategy.createInstance(WithMultipleConstructors.class);

        assertThat(result.invokedConstructor).isEqualTo(EXPECTED_CONSTRUCTOR);
    }

    @Test
    void shouldReturnNullIfConstructorThrowsAnError() {
        assertThat(strategy.createInstance(ConstructorThrowingError.class)).isNull();
    }

    @Test
    void shouldExcludeBuilderConstructor() {
        assertThat(strategy.createInstance(PojoWithBuilder.class)).isNull();
    }

    // Used via reflection
    @SuppressWarnings("unused")
    private static class PojoWithBuilder {
        private PojoWithBuilder(final Builder builder) {
        }

        private static final class Builder {}
    }

    // Used via reflection
    @SuppressWarnings("unused")
    private static class ConstructorThrowingError {
        private ConstructorThrowingError(String foo) {
            throw new RuntimeException("Expected error");
        }
    }

    // Used via reflection
    @SuppressWarnings("unused")
    private static class WithMultipleConstructors {
        private final AssertionError shouldNotBeInvoked = new AssertionError("Should not be invoked");
        private final String invokedConstructor;

        private WithMultipleConstructors() {
            throw shouldNotBeInvoked;
        }

        private WithMultipleConstructors(Builder builder) {
            throw shouldNotBeInvoked;
        }

        private WithMultipleConstructors(int i, Object o, Boolean b, Long l) {
            throw shouldNotBeInvoked;
        }

        /**
         * Non-default constructor with the least number of arguments.
         * Should be invoked with default values for each type.
         */
        private WithMultipleConstructors(int i, Object o, Boolean b) {
            assertThat(i).isZero();
            assertThat(o).isNull();
            assertThat(b).isNull();

            this.invokedConstructor = EXPECTED_CONSTRUCTOR;
        }

        private WithMultipleConstructors(int i, Object o, Boolean b, Long l, double d) {
            throw shouldNotBeInvoked;
        }

        private static final class Builder {}
    }
}
