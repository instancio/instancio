/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.PrimitiveFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.ON_COMPLETE,
        Feature.AFTER_GENERATE,
        Feature.SELECTOR,
        Feature.SET
})
@ExtendWith(InstancioExtension.class)
class CustomGeneratorWithPrimitiveFieldsTest {

    private static final int SAMPLE_SIZE = 250;

    private static Generator<PrimitiveFields> generator(final AfterGenerate afterGenerate) {
        return new Generator<PrimitiveFields>() {
            @Override
            public PrimitiveFields generate(final Random random) {
                return PrimitiveFields.builder().build();
            }

            @Override
            public Hints hints() {
                return Hints.afterGenerate(afterGenerate);
            }
        };
    }

    private List<PrimitiveFields> create(final Generator<?> generator, final int size) {
        return Instancio.of(PrimitiveFields.class)
                .supply(all(PrimitiveFields.class), generator)
                .stream()
                .limit(size)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("POPULATE_NULLS: nothing should be populated since all fields are primitive")
    void populateNulls() {
        final PrimitiveFields result = create(generator(AfterGenerate.POPULATE_NULLS), 1).get(0);

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
    @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: all should be populated")
    void populateNullsAndDefaultPrimitives() {
        final List<PrimitiveFields> results = create(generator(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES), SAMPLE_SIZE);

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
