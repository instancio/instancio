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
package org.instancio.test.features.conditional;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.valueOf;

@FeatureTag({Feature.CONDITIONAL, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class ConditionalWithRegularSelectorTest {

    @Test
    void fieldSelectorWithoutClass() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(field(StringFields::getTwo), "2")
                // using field(String) selector (without specifying the target class)
                .when(valueOf(field("two")).is("2").set(field("one"), "1"))
                .create();

        assertThat(result.getOne()).isEqualTo("1");
        assertThat(result.getTwo()).isEqualTo("2");
    }

}
