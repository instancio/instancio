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
package org.instancio.test.features.setmodel;

import lombok.Data;
import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class SetModelNestedModelsTest {

    //@formatter:off
    private static @Data class Outer { Mid mid1; Mid mid2; }
    private static @Data class Mid { Inner inner1; Inner inner2; }
    private static @Data class Inner { String value; }
    //@formatter:on

    private static final String INNER_VALUE = "-inner-";

    private static Stream<Arguments> stringModel() {
        return Stream.of(
                Arguments.of(Gen.text().pattern(INNER_VALUE).toModel()),
                Arguments.of(Instancio.of(String.class).set(allStrings(), INNER_VALUE).toModel())
        );
    }

    @MethodSource("stringModel")
    @ParameterizedTest
    void deeplyNestedSetModels(final Model<String> stringModel) {
        final Model<Inner> innerModel = Instancio.of(Inner.class)
                .setModel(allStrings(), stringModel)
                .toModel();

        final Model<Mid> midModel = Instancio.of(Mid.class)
                .setModel(field(Mid::getInner1), innerModel)
                .toModel();

        final Model<Outer> outerModel = Instancio.of(Outer.class)
                .setModel(field(Outer::getMid1), midModel)
                .toModel();

        final List<Outer> results = Instancio.ofList(Outer.class)
                .setModel(all(Outer.class), outerModel)
                .create();

        assertThat(results).isNotEmpty().allSatisfy(result -> {

            // mid1
            assertThat(result.getMid1().getInner1().getValue()).isEqualTo(INNER_VALUE);
            assertThat(result.getMid1().getInner2().getValue()).isNotEqualTo(INNER_VALUE);
            // mid2
            assertThat(result.getMid2().getInner2().getValue()).isNotEqualTo(INNER_VALUE);
            assertThat(result.getMid2().getInner2().getValue()).isNotEqualTo(INNER_VALUE);
        });
    }

    /**
     * {@code field(Mid::getInner1)} model is set twice.
     */
    @Test
    void setModelTwice() {
        final String innerValue = "-inner-";

        final Model<Inner> innerModel = Instancio.of(Inner.class)
                .set(allStrings(), innerValue)
                .toModel();

        // set field(Mid::getInner1) once here
        final Model<Mid> midModel = Instancio.of(Mid.class)
                .setModel(field(Mid::getInner1), innerModel)
                .toModel();

        // set field(Mid::getInner1) also here
        final Outer result = Instancio.of(Outer.class)
                .setModel(field(Mid::getInner1), innerModel)
                .setModel(field(Outer::getMid1), midModel)
                .create();

        // mid1
        assertThat(result.getMid1().getInner1().getValue()).isEqualTo(innerValue);
        assertThat(result.getMid1().getInner2().getValue()).isNotEqualTo(innerValue);
        // mid2
        assertThat(result.getMid2().getInner2().getValue()).isNotEqualTo(innerValue);
        assertThat(result.getMid2().getInner2().getValue()).isNotEqualTo(innerValue);
    }

    @Test
    void ignore() {
        final Model<Inner> innerModel = Instancio.of(Inner.class)
                .ignore(allStrings())
                .toModel();

        final Model<Mid> midModel = Instancio.of(Mid.class)
                .setModel(field(Mid::getInner1), innerModel)
                .toModel();

        final Outer result = Instancio.of(Outer.class)
                .setModel(field(Outer::getMid1), midModel)
                .create();

        // mid1
        assertThat(result.getMid1().getInner1().getValue()).isNull();
        assertThat(result.getMid1().getInner2().getValue()).isNotNull();
        // mid2
        assertThat(result.getMid2().getInner2().getValue()).isNotNull();
        assertThat(result.getMid2().getInner2().getValue()).isNotNull();
    }
}
