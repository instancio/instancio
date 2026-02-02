/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.FILTER, Feature.CARTESIAN_PRODUCT})
@ExtendWith(InstancioExtension.class)
class FilterWithCartesianProductTest {

    @Test
    void cartesianProduct() {
        final List<StringFields> results = Instancio.ofCartesianProduct(StringFields.class)
                .with(field(StringFields::getOne), "one")
                .with(field(StringFields::getTwo), "two")
                .filter(all(field(StringFields::getThree), field(StringFields::getFour)), (String s) -> s.length() == 5)
                .create();

        assertThat(results)
                .singleElement()
                .satisfies(result -> {
                    assertThat(result.getOne()).isEqualTo("one");
                    assertThat(result.getTwo()).isEqualTo("two");
                    assertThat(result.getThree()).hasSize(5);
                    assertThat(result.getFour()).hasSize(5);
                });
    }
}