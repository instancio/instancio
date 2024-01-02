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
package org.instancio.internal.util;

import org.instancio.exception.InstancioException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionUtilsTest {

    @Nested
    class CausedByTest {
        @Test
        void getCausedIsNull() {
            assertThat(ExceptionUtils.getCausedBy(null)).isEmpty();
        }

        @Test
        void singleCauseWithNoMessage() {
            assertThat(ExceptionUtils.getCausedBy(new Throwable()))
                    .isEqualTo(String.format("%n => caused by: Throwable"));
        }

        @Test
        void multipleCausesWithMessages() {
            final InstancioException ex = new InstancioException("top",
                    new RuntimeException("cause",
                            new NullPointerException("root")));

            assertThat(ExceptionUtils.getCausedBy(ex))
                    .isEqualTo(String.format("%n" +
                            " => caused by: InstancioException: \"top\"%n" +
                            " => caused by: RuntimeException: \"cause\"%n" +
                            " => caused by: NullPointerException: \"root\""));
        }

        @Test
        void multipleCausesWithoutMessages() {
            final InstancioException ex = new InstancioException("top",
                    new RuntimeException(
                            new NullPointerException()));

            assertThat(ExceptionUtils.getCausedBy(ex))
                    .isEqualTo(String.format("%n" +
                            " => caused by: InstancioException: \"top\"%n" +
                            " => caused by: RuntimeException: \"java.lang.NullPointerException\"%n" +
                            " => caused by: NullPointerException"));
        }
    }
}
