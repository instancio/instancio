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
package org.instancio.test.features.model;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.generator.specs.StringSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class ModelWithValueSpecTest {

    @Test
    void valueSpec() {
        final Model<Phone> model = Instancio.of(Phone.class)
                .generate(field(Phone::getNumber), Gen.string().digits())
                .toModel();

        final Phone result = Instancio.create(model);

        assertThat(result.getNumber()).containsOnlyDigits();
    }

    @Test
    void modifyingSpecAfterPassingToModel() {
        final StringSpec phoneNumber = Gen.string().digits();

        final Model<Phone> model = Instancio.of(Phone.class)
                .generate(field(Phone::getNumber), phoneNumber)
                .toModel();

        // Modifying the string spec after the model has been creating
        final String prefix = "updated-";
        phoneNumber.prefix(prefix);

        final Phone result = Instancio.create(model);

        assertThat(result.getNumber()).startsWith(prefix);
    }
}
