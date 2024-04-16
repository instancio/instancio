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
package org.instancio.test.features.ofset;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.OF_SET, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class OfSetFromModelTest {

    private static final String EXPECTED_NUMBER = "12345";
    private static final int EXPECTED_SIZE = 3;

    private final Model<Phone> model = Instancio.of(Phone.class)
            .set(field(Phone::getNumber), EXPECTED_NUMBER)
            .toModel();

    @Test
    void ofSetFromModelCreate() {
        final Set<Phone> result = Instancio.ofSet(model)
                .size(EXPECTED_SIZE)
                .create();

        assertMatchesModel(result);
    }

    @Test
    void withSeedShouldCreateDistinctCollectionElements() {
        final Set<Phone> result = Instancio.ofSet(model)
                .size(EXPECTED_SIZE)
                .withSeed(-1)
                .create();

        assertMatchesModel(new HashSet<>(result));
    }

    @Test
    void customiseObjectCreatedFromModel() {
        final String newNumber = "98765";
        final Set<Phone> result = Instancio.ofSet(model)
                .size(EXPECTED_SIZE)
                .set(field(Phone::getNumber), newNumber)
                .create();

        assertThat(result)
                .as("Should have new number")
                .hasSize(EXPECTED_SIZE)
                .allMatch(p -> newNumber.equals(p.getNumber()));
    }

    @Test
    void withSeedShouldCreateEqualCollections() {
        final Set<Phone> result1 = Instancio.ofSet(model)
                .size(EXPECTED_SIZE)
                .withSeed(-1)
                .create();

        final Set<Phone> result2 = Instancio.ofSet(model)
                .size(EXPECTED_SIZE)
                .withSeed(-1)
                .create();

        assertThat(result1)
                .hasSize(EXPECTED_SIZE)
                .isEqualTo(result2);
    }

    @Test
    void nestedSetsFromModel() {
        final Model<Set<String>> model = Instancio.ofSet(String.class)
                .generate(allStrings(), gen -> gen.string().length(1))
                .toModel();

        final Set<Set<String>> results = Instancio.ofSet(model).create();

        assertThat(results).isNotEmpty().allSatisfy(nested ->
                assertThat(nested).isNotEmpty().allSatisfy(s ->
                        assertThat(s).hasSize(1)));
    }

    private static void assertMatchesModel(final Collection<Phone> actual) {
        assertThat(actual)
                .hasSize(EXPECTED_SIZE)
                .allMatch(p -> EXPECTED_NUMBER.equals(p.getNumber()));
    }
}
