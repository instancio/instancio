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
package org.instancio.test.features.generator.custom.map;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.MapHint;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@ExtendWith(InstancioExtension.class)
class UnmodifiableMapGeneratorTest {

    private static final Map<String, Integer> UNMODIFIABLE_MAP = Collections.emptyMap();

    @EnumSource(AfterGenerate.class)
    @ParameterizedTest
    void createUnmodifiableMap(final AfterGenerate afterGenerate) {
        final Map<String, Integer> result = Instancio.of(new TypeToken<Map<String, Integer>>() {})
                .supply(all(Map.class), new CustomMapGenerator(afterGenerate))
                .create();

        assertThat(result).isSameAs(UNMODIFIABLE_MAP);
    }

    /**
     * Since the hint does not specify {@link MapHint#generateEntries()},
     * none of the action types should try to add entries to the map.
     */
    private static final class CustomMapGenerator implements Generator<Map<String, Integer>> {
        private final AfterGenerate afterGenerate;

        private CustomMapGenerator(final AfterGenerate afterGenerate) {
            this.afterGenerate = afterGenerate;
        }

        @Override
        public Map<String, Integer> generate(final Random random) {
            return UNMODIFIABLE_MAP;
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(afterGenerate);
        }
    }
}
