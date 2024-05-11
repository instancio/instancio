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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneType;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.FILTER, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class FilterWithSubtypeTest {

    @Test
    void subtype() {
        final Phone result = Instancio.of(Phone.class)
                .subtype(all(Phone.class), PhoneWithType.class)
                .filter(all(Phone.class), (PhoneWithType p) -> p.getPhoneType() == PhoneType.HOME)
                .create();

        assertThat(((PhoneWithType) result).getPhoneType()).isEqualTo(PhoneType.HOME);
    }
}