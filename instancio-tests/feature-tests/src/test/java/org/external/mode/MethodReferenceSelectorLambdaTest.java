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
package org.external.mode;

import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag(Feature.METHOD_REFERENCE_SELECTOR)
class MethodReferenceSelectorLambdaTest {

    @Test
    @SuppressWarnings("Convert2MethodRef")
    void methodReferenceSelectorDoesNotAcceptLambdaFunction() {
        assertThatThrownBy(() -> field((StringHolder holder) -> holder.getValue()))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Unable to resolve the field from method reference",
                        "The method reference is expressed as a lambda function",
                        "Example:     field((SamplePojo pojo) -> pojo.getValue())",
                        "Instead of:  field(SamplePojo::getValue)");
    }
}
