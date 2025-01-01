/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.cartesianproduct;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Cartesian product cannot be saved as a model,
 * but can be created from a model.
 */
@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class CartesianProductModelTest {

    @Test
    void fromModel() {
        final Model<Person> model = Instancio.of(Person.class)
                .set(field(Person::getName), "foo")
                .toModel();

        final List<Person> results = Instancio.ofCartesianProduct(model)
                .with(field(Person::getGender), Gender.MALE, Gender.FEMALE)
                .with(field(Person::getAge), 30, 40)
                .create();

        assertThat(results)
                .hasSize(4)
                .allMatch(p -> "foo".equals(p.getName()));

        assertThat(results)
                .extracting(Person::getGender)
                .containsExactly(Gender.MALE, Gender.MALE, Gender.FEMALE, Gender.FEMALE);

        assertThat(results)
                .extracting(Person::getAge)
                .containsExactly(30, 40, 30, 40);
    }
}
