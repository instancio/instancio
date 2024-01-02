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
package org.external;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.constraints.Size;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This test is within {@code org.external} package because reported locations
 * filter stacktrace elements containing {@code org.instancio}.
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class InvalidAnnotationSpecTest {

    private static class Pojo {
        // intentionally invalid range
        @SuppressWarnings("MinMaxValuesInspection")
        @Size(min = 100, max = 3)
        String value;
    }

    @Test
    void shouldFailWithErrorMessage() {
        assertThatThrownBy(() -> Instancio.create(Pojo.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "Error creating an object",
                        " -> at org.external.InvalidAnnotationSpecTest", "(InvalidAnnotationSpecTest.java:47)",
                        "Reason: invalid bean validation annotation: min must be less than or equal to max: min=100, max=3");
    }
}
