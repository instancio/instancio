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
package org.instancio.test.features.populate;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.PopulationStrategy;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.cartesian.CartesianTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.POPULATE)
@ExtendWith(InstancioExtension.class)
class PopulateNestedObjectTest {

    /**
     * Given an object with nested objects that are <b>not</b> {@code null},
     * should populate fields of nested objects and apply selectors.
     */
    @EnumSource(value = PopulationStrategy.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldPopulateInitialisedNestedObjects(final PopulationStrategy populationStrategy) {
        final StringsAbc object = StringsAbc.builder()
                .a("A")
                .def(StringsDef.builder()
                        .d("D")
                        .ghi(StringsGhi.builder().i("I").build())
                        .build())
                .build();

        Instancio.ofObject(object)
                .withPopulationStrategy(populationStrategy)
                .set(field(StringsAbc::getB), "B")
                .set(field(StringsDef::getE), "E")
                .set(field(StringsGhi::getH), "H")
                .populate();

        assertThat(object.getA()).isEqualTo("A");
        assertThat(object.getB()).isEqualTo("B");
        assertThat(object.getDef().getD()).isEqualTo("D");
        assertThat(object.getDef().getE()).isEqualTo("E");
        assertThat(object.getDef().getGhi().getH()).isEqualTo("H");
        assertThat(object.getDef().getGhi().getI()).isEqualTo("I");
        assertThatObject(object).isFullyPopulated();
    }

    /**
     * Given a shallow object, with nested objects that are {@code null},
     * should populate fields of nested objects and apply selectors.
     */
    @CartesianTest
    void populateWithOverwriteExistingValues(
            @CartesianTest.Enum(names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"}) PopulationStrategy strategy,
            @CartesianTest.Values(booleans = {true, false}) boolean overwriteExistingValues) {

        final StringsAbc object = StringsAbc.builder().a("A").build();

        Instancio.ofObject(object)
                .withSettings(Settings.create()
                        .set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues)
                        .set(Keys.POPULATION_STRATEGY, strategy))
                .set(field(StringsAbc::getB), "B")
                .set(field(StringsDef::getE), "E") // nested object property
                .set(field(StringsGhi::getH), "H") // nested object property
                .populate();

        assertThat(object.getA()).isEqualTo("A");
        assertThat(object.getB()).isEqualTo("B");
        assertThat(object.getDef().getE()).isEqualTo("E");
        assertThat(object.getDef().getGhi().getH()).isEqualTo("H");
        assertThatObject(object).isFullyPopulated();
    }
}
