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
package org.instancio.test.features.filter;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag({Feature.FILTER, Feature.IGNORE})
@ExtendWith(InstancioExtension.class)
class FilterWithIgnoreTest {

    @Test
    void ignore() {
        final InstancioApi<StringFields> api = Instancio.of(StringFields.class)
                .ignore(field(StringFields::getOne))
                .filter(field(StringFields::getOne), val -> true); // don't reject any value

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContainingAll(
                        "Unused selector in: filter()",
                        "1: field(StringFields, \"one\")");
    }
}