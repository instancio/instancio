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
package org.instancio.test.features.supply;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;

@FeatureTag(Feature.SUPPLY)
@ExtendWith(InstancioExtension.class)
class SupplyThrowsExceptionTest {

    @Test
    void supplyShouldPropagateException() {
        final RuntimeException exception = new RuntimeException();

        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .supply(allStrings(), () -> {
                    throw exception;
                });

        assertThatThrownBy(api::create)
                .hasMessageContaining("Exception thrown by a custom Generator or Supplier")
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasRootCause(exception);
    }
}
