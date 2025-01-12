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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.PopulationStrategy;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.POPULATE, Feature.SUBTYPE})
@ExtendWith(InstancioExtension.class)
class PopulateSubtypeTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Container {
        StringHolderInterface stringHolder;
    }

    @EnumSource(value = PopulationStrategy.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void withSubtypeAndNullObject(final PopulationStrategy populationStrategy) {
        final Container object = new Container(); // leave Container.stringHolder as null

        Instancio.ofObject(object)
                .withPopulationStrategy(populationStrategy)
                .subtype(all(StringHolderInterface.class), StringHolder.class)
                .populate();

        assertThat(object.stringHolder).isExactlyInstanceOf(StringHolder.class);
        assertThat(object.stringHolder.getValue()).isNotBlank();
    }

    /**
     * Even though we initialised the Container's field to StringHolder,
     * the subtype must still be specified explicitly
     * for the StringHolder's field to be resolved.
     */
    @Nested
    class SubtypeWithBlankObjectTest {
        // leave StringHolder object as blank
        private final StringHolder stringHolder = new StringHolder();
        private final Container object = new Container(stringHolder);

        @EnumSource(PopulationStrategy.class)
        @ParameterizedTest
        void subtypeNotSpecified(final PopulationStrategy populationStrategy) {
            Instancio.ofObject(object)
                    .withPopulationStrategy(populationStrategy)
                    .populate();

            assertThat(object.stringHolder).isSameAs(stringHolder);
            assertThat(object.stringHolder.getValue())
                    .as("Field not resolved since subtype() was not specified")
                    .isNull();
        }

        @EnumSource(value = PopulationStrategy.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void withSubtype(final PopulationStrategy populationStrategy) {
            Instancio.ofObject(object)
                    .withPopulationStrategy(populationStrategy)
                    .subtype(all(StringHolderInterface.class), StringHolder.class)
                    .populate();

            assertThat(object.stringHolder).isSameAs(stringHolder);
            assertThat(object.stringHolder.getValue())
                    .as("Should generate random value")
                    .isNotBlank();
        }

        @EnumSource(PopulationStrategy.class)
        @ParameterizedTest
        void withSubtypeAndSet(final PopulationStrategy populationStrategy) {
            Instancio.ofObject(object)
                    .withPopulationStrategy(populationStrategy)
                    .set(field(StringHolder::getValue), "foo")
                    .subtype(all(StringHolderInterface.class), StringHolder.class)
                    .populate();

            assertThat(object.stringHolder).isSameAs(stringHolder);
            assertThat(object.stringHolder.getValue()).isEqualTo("foo");
        }
    }

    @Nested
    class SubtypeWithNonBlankObjectTest {
        // initialise StringHolder.value
        private final StringHolder stringHolder = new StringHolder("foo");
        private final Container object = new Container(stringHolder);

        @EnumSource(PopulationStrategy.class)
        @ParameterizedTest
        void subtypeNotSpecified(final PopulationStrategy populationStrategy) {
            Instancio.ofObject(object)
                    .withPopulationStrategy(populationStrategy)
                    .populate();

            assertThat(object.stringHolder).isSameAs(stringHolder);
            assertThat(object.stringHolder.getValue()).isEqualTo("foo");
        }

        @EnumSource(value = PopulationStrategy.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void withSubtype(final PopulationStrategy populationStrategy) {
            Instancio.ofObject(object)
                    .withPopulationStrategy(populationStrategy)
                    .subtype(all(StringHolderInterface.class), StringHolder.class)
                    .populate();

            assertThat(object.stringHolder).isSameAs(stringHolder);
            assertThat(object.stringHolder.getValue())
                    .as("Should not overwrite initialised field")
                    .isEqualTo("foo");
        }

        @EnumSource(value = PopulationStrategy.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void withSubtypeAndSet(final PopulationStrategy populationStrategy) {

            Instancio.ofObject(object)
                    .withPopulationStrategy(populationStrategy)
                    .set(field(StringHolder::getValue), "bar")
                    .subtype(all(StringHolderInterface.class), StringHolder.class)
                    .populate();

            assertThat(object.stringHolder).isSameAs(stringHolder);
            assertThat(object.stringHolder.getValue()).isEqualTo("bar");
        }
    }
}