/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.generator.oneof;

import org.instancio.Instancio;
import org.instancio.InstancioMetaModel;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Address_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@InstancioMetaModel(classes = Address.class)
@FeatureTag(Feature.ONE_OF_ARRAY_GENERATOR)
@ExtendWith(InstancioExtension.class)
class OneOfArrayGeneratorTest {

    @Test
    void oneOf() {
        final Address result = Instancio.of(Address.class)
                .generate(Address_.city, gen -> gen.oneOf("one"))
                .generate(Address_.country, gen -> gen.oneOf("two"))
                .create();

        assertThat(result.getCity()).isEqualTo("one");
        assertThat(result.getCountry()).isEqualTo("two");
    }
}
