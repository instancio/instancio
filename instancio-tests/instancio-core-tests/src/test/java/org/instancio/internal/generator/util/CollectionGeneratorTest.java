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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class CollectionGeneratorTest {
    private static final int MIN_SIZE = 101;
    private static final int MAX_SIZE = 102;
    private static final int SAMPLE_SIZE = 10_000;
    private static final int PERCENTAGE_THRESHOLD = 10;

    private final Settings settings = Settings.defaults()
            .set(Keys.COLLECTION_MIN_SIZE, MIN_SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, MAX_SIZE);

    private final Random random = new DefaultRandom();
    private final GeneratorContext context = new GeneratorContext(settings, random);

    private CollectionGenerator<?> generator() {
        return new CollectionGenerator<>(context);
    }

    @Test
    void apiMethod() {
        assertThat(generator().apiMethod()).isEqualTo("collection()");
    }

    @Test
    @DisplayName("Should generate either an empty collection or null")
    void generateNullableCollection() {
        final Set<Object> results = new HashSet<>();
        final int[] counts = new int[2];

        settings.set(Keys.COLLECTION_NULLABLE, true)
                .set(Keys.COLLECTION_ELEMENTS_NULLABLE, true);

        final CollectionGenerator<?> generator = generator();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Collection<?> result = generator.generate(random);
            results.add(result);
            counts[result == null ? 0 : 1]++;
        }

        assertThat(results).containsNull()
                .hasAtLeastOneElementOfType(ArrayList.class)
                .hasSize(2); // null and empty list

        assertThat(counts[1])
                .as("Expecting 5/6 of results to be non-null")
                .isCloseTo((5 * SAMPLE_SIZE) / 6, withPercentage(PERCENTAGE_THRESHOLD));

        HintsAssert.assertHints(generator.hints())
                .collectionHintGenerateElementsIsBetween(MIN_SIZE, MAX_SIZE)
                .nullableCollectionElements(true)
                .afterGenerate(AfterGenerate.POPULATE_ALL);
    }

    @Test
    void shouldFailOnInstantiationError() {
        final CollectionGenerator<?> generator = generator().subtype(List.class);

        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioTerminatingException.class)
                .hasMessageContainingAll(
                        "Please submit a bug report",
                        "Error creating instance of: interface java.util.List");
    }
}
