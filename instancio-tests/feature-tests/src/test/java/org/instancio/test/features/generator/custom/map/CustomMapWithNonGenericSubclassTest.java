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
package org.instancio.test.features.generator.custom.map;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.MapHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfMap;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({Feature.GENERATOR, Feature.MAP_GENERATOR_SUBTYPE})
@ExtendWith(InstancioExtension.class)
class CustomMapWithNonGenericSubclassTest {

    private static final int GENERATE_ENTRIES = 3;

    private static class CustomMapGenerator implements Generator<NonGenericSubclassOfMap> {

        @Override
        public NonGenericSubclassOfMap generate(final Random random) {
            return new NonGenericSubclassOfMap();
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .afterGenerate(AfterGenerate.APPLY_SELECTORS)
                    .with(MapHint.builder().generateEntries(GENERATE_ENTRIES).build())
                    .build();
        }
    }

    @Test
    void customMapCreatedByCustomGenerator() {
        final NonGenericSubclassOfMap result = Instancio.of(NonGenericSubclassOfMap.class)
                .supply(types().of(Map.class), new CustomMapGenerator())
                .create();

        assertThat(result)
                .isExactlyInstanceOf(NonGenericSubclassOfMap.class)
                .hasSize(GENERATE_ENTRIES)
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }

    @Test
    void customMapCreatedViaSubtype() {
        final Map<String, Long> result = Instancio.of(new TypeToken<Map<String, Long>>() {})
                .subtype(types().of(Map.class), NonGenericSubclassOfMap.class)
                .create();

        assertThat(result)
                .isExactlyInstanceOf(NonGenericSubclassOfMap.class)
                .isNotEmpty()
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }

    @Test
    void customMapCreatedViaGeneratorSubtype() {
        final Map<String, Long> result = Instancio.of(new TypeToken<Map<String, Long>>() {})
                .generate(types().of(Map.class), gen -> gen.map().subtype(NonGenericSubclassOfMap.class))
                .create();

        assertThat(result)
                .isExactlyInstanceOf(NonGenericSubclassOfMap.class)
                .isNotEmpty()
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }
}
