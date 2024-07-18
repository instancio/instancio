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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.InstanceProvider;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.conditions.Conditions.RANDOM_STRING;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(InstancioExtension.class)
class GivenStreamTest {

    @Nested
    class WithoutCustomProviderTest {
        private @Given Stream<String> field;

        @Test
        void field() {
            assertThat(field.limit(Constants.SAMPLE_SIZE_DD)).are(RANDOM_STRING);
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void param(@Given final Stream<String> param) {
            assertThat(param.limit(Constants.SAMPLE_SIZE_DD)).are(RANDOM_STRING);
        }

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void paramWithInstancioSource(@Given final Stream<String> param) {
            assertThat(param.limit(Constants.SAMPLE_SIZE_DD)).are(RANDOM_STRING);
        }
    }

    @Nested
    class WithCustomProviderTest {
        private static class CustomProvider implements InstanceProvider {
            @Override
            public Object provide(final ElementContext context) {
                return context.random().trueOrFalse() ? "foo" : "bar";
            }
        }

        private @Given(CustomProvider.class) Stream<String> field;

        @Test
        void field() {
            assertThat(field.limit(Constants.SAMPLE_SIZE_DD))
                    .containsOnly("foo", "bar");
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void param(@Given(CustomProvider.class) final Stream<String> param) {
            assertThat(param.limit(Constants.SAMPLE_SIZE_DD))
                    .containsOnly("foo", "bar");
        }

        @InstancioSource(samples = 5)
        @ParameterizedTest
        void paramWithInstancioSource(@Given(CustomProvider.class) final Stream<String> param) {
            assertThat(param.limit(Constants.SAMPLE_SIZE_DD))
                    .containsOnly("foo", "bar");
        }
    }
}
