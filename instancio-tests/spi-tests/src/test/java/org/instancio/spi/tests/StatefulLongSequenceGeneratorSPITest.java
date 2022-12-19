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
package org.instancio.spi.tests;

import org.example.generator.LongSequenceGenerator;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class StatefulLongSequenceGeneratorSPITest {

    @Test
    void shouldGenerateListContainingSequenceOfLongs() {
        final List<Long> results = Instancio.create(new TypeToken<List<Long>>() {});

        final int seqLength = results.size();
        for (int i = 0; i < seqLength; i++) {
            assertThat(results.get(i))
                    .as("Expected a sequence [%s..%s] in increments of one",
                            LongSequenceGenerator.START_FROM, LongSequenceGenerator.START_FROM + seqLength)
                    .isEqualTo(LongSequenceGenerator.START_FROM + i);
        }
    }

    /**
     * For <b>generators loaded via SPI</b>, a new generator is instantiated
     * for each stream element. This is an implementation detail due to the fact
     * that {@code GeneratorProviderFacade} is instantiated within the engine.
     * <p>
     * TODO: this should probably be refactored so that the model context the providers
     *  (both, generator and type resolver)
     */
    @Test
    @DisplayName("Stream results are independent of each other")
    void stream() {
        final Set<Long> results = Instancio.of(Long.class)
                .stream()
                .limit(10)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly(LongSequenceGenerator.START_FROM);
    }

    @Test
    void sequenceShouldBeReset() {
        assertSequence(LongSequenceGenerator.START_FROM);

        // generator should be instantiated again, resetting the sequence
        assertSequence(LongSequenceGenerator.START_FROM);
    }

    private static void assertSequence(final long startingValue) {
        final TwoListsOfLong result = Instancio.of(TwoListsOfLong.class)
                .withSettings(Settings.create()
                        .set(Keys.COLLECTION_MIN_SIZE, 2)
                        .set(Keys.COLLECTION_MAX_SIZE, 2))
                .create();

        assertThat(result.getList1()).containsExactly(startingValue, startingValue + 1);
        assertThat(result.getList2())
                .as("Generator sequence should continue if used with multiple fields")
                .containsExactly(startingValue + 2, startingValue + 3);
    }
}
