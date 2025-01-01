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
package org.external.errorhandling;

import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class AbstractErrorMessageTestTemplate {

    abstract void methodUnderTest();

    abstract String expectedMessage();

    protected Class<?> expectedException() {
        return InstancioApiException.class;
    }

    @Test
    final void verifyErrorMessage() {
        assertThatThrownBy(this::methodUnderTest)
                .isExactlyInstanceOf(expectedException())
                // replace \r to prevent test failures when run on Windows
                .extracting(ex -> ex.getMessage().replace("\r", ""))
                .isEqualTo(expectedMessage());
    }
}
