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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class MapGeneratorTest {
    private static final int MIN_SIZE = 101;
    private static final int MAX_SIZE = 102;
    private static final int SAMPLE_SIZE = 10_000;
    private static final int PERCENTAGE_THRESHOLD = 10;

    private final Settings settings = Settings.defaults()
            .set(Keys.MAP_MIN_SIZE, MIN_SIZE)
            .set(Keys.MAP_MAX_SIZE, MAX_SIZE);

    private final Random random = new DefaultRandom();
    private final GeneratorContext context = new GeneratorContext(settings, random);

    private MapGenerator<?, ?> generator() {
        return new MapGenerator<>(context);
    }

    @Test
    void apiMethod() {
        assertThat(generator().apiMethod()).isEqualTo("map()");
    }

    @Test
    @DisplayName("Should generate either an empty map or null")
    void generateNullableMap() {
        final Set<Object> results = new HashSet<>();
        final int[] counts = new int[2];

        settings.set(Keys.MAP_NULLABLE, true)
                .set(Keys.MAP_KEYS_NULLABLE, true)
                .set(Keys.MAP_VALUES_NULLABLE, true);

        final MapGenerator<?, ?> generator = generator();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Map<?, ?> result = generator.generate(random);
            results.add(result);
            counts[result == null ? 0 : 1]++;
        }

        assertThat(results).containsNull()
                .hasAtLeastOneElementOfType(HashMap.class)
                .hasSize(2); // null and empty map

        assertThat(counts[1])
                .as("Expecting 5/6 of results to be non-null")
                .isCloseTo((5 * SAMPLE_SIZE) / 6, withPercentage(PERCENTAGE_THRESHOLD));

        HintsAssert.assertHints(generator.hints())
                .mapHintGenerateEntriesIsBetween(MIN_SIZE, MAX_SIZE)
                .nullableMapKeys(true)
                .nullableMapValues(true)
                .afterGenerate(AfterGenerate.POPULATE_ALL);
    }

    @Test
    void shouldNotFailOnInstantiationErrorByDefault() {
        final MapGenerator<?, ?> generator = generator().subtype(Map.class);

        assertThat(generator.generate(random)).isNull();
    }

    @Test
    void shouldFailOnInstantiationErrorWhenFailOnErrorIsEnabled() {
        settings.set(Keys.FAIL_ON_ERROR, true);

        final MapGenerator<?, ?> generator = generator().subtype(Map.class);

        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioTerminatingException.class)
                .hasMessageContainingAll(
                        "Please submit a bug report",
                        "Error creating instance of: interface java.util.Map");
    }
}
