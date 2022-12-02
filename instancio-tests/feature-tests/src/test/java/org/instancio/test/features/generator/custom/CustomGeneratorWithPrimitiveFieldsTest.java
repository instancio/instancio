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
package org.instancio.test.features.generator.custom;

import org.instancio.Generator;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.test.support.pojo.basic.PrimitiveFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.ON_COMPLETE,
        Feature.POPULATE_ACTION,
        Feature.SELECTOR,
        Feature.SET
})
class CustomGeneratorWithPrimitiveFieldsTest {

    private static final int SAMPLE_SIZE = 250;

    private static class PrimitiveFieldsGenerator implements Generator<PrimitiveFields> {
        private final PopulateAction populateAction;

        private PrimitiveFieldsGenerator(final PopulateAction populateAction) {
            this.populateAction = populateAction;
        }

        @Override
        public PrimitiveFields generate(final Random random) {
            return new PrimitiveFields();
        }

        @Override
        public Hints hints() {
            return Hints.withPopulateAction(populateAction);
        }
    }

    private final Generator<?> generatorActionPopulateNulls = new PrimitiveFieldsGenerator(PopulateAction.NULLS);
    private final Generator<?> generatorActionPopulateNullsAndPrimitives = new PrimitiveFieldsGenerator(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);

    private List<PrimitiveFields> create(final Generator<?> generator, final int size) {
        return Instancio.of(PrimitiveFields.class)
                .supply(all(PrimitiveFields.class), generator)
                .stream()
                .limit(size)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("Action NULLS: nothing should be populated since all fields are primitive")
    void populateNullsAction() {
        final PrimitiveFields result = create(generatorActionPopulateNulls, 1).get(0);

        assertThat(generatorActionPopulateNulls.hints().populateAction())
                .isEqualTo(PopulateAction.NULLS);

        assertThat(result.getByteValue()).isZero();
        assertThat(result.getShortValue()).isZero();
        assertThat(result.getIntValue()).isZero();
        assertThat(result.getLongValue()).isZero();
        assertThat(result.getFloatValue()).isZero();
        assertThat(result.getDoubleValue()).isZero();
        assertThat(result.isBooleanValue()).isFalse();
        assertThat(result.getCharValue()).isEqualTo('\u0000');
    }

    @Test
    @DisplayName("Action NULLS_AND_PRIMITIVES: all should be populated")
    void populateNullsAndPrimitivesAction() {
        final List<PrimitiveFields> results = create(generatorActionPopulateNullsAndPrimitives, SAMPLE_SIZE);

        assertThat(generatorActionPopulateNullsAndPrimitives.hints().populateAction())
                .isEqualTo(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);

        assertThat(results).allSatisfy(result -> {
            assertThat(result.getByteValue()).isNotZero();
            assertThat(result.getShortValue()).isNotZero();
            assertThat(result.getIntValue()).isNotZero();
            assertThat(result.getLongValue()).isNotZero();
            assertThat(result.getFloatValue()).isNotZero();
            assertThat(result.getDoubleValue()).isNotZero();
            assertThat(result.getCharValue()).isNotEqualTo('\u0000');
        });

        assertThat(results).extracting(PrimitiveFields::isBooleanValue)
                .contains(true, false);
    }

}
