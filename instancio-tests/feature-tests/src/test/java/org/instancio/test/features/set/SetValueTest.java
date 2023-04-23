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
package org.instancio.test.features.set;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.SET)
class SetValueTest {

    @Test
    void setValue() {
        final Person expected = Instancio.create(Person.class);

        final Person result = Instancio.of(Person.class)
                .set(field("name"), expected.getName())
                .set(field("address"), expected.getAddress())
                .create();

        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getAddress()).isSameAs(expected.getAddress());
    }

}
