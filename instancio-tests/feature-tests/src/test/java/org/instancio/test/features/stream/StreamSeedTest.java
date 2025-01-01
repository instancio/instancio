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
package org.instancio.test.features.stream;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.STREAM, Feature.WITH_SEED, Feature.WITH_SEED_ANNOTATION})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class StreamSeedTest {
    private static final long SEED = 123;
    private static final int SAMPLE_SIZE = 10;

    @SuppressWarnings("FieldCanBeLocal")
    private static Set<UUID> first;

    @Seed(SEED)
    @Order(1)
    @Test
    @DisplayName("1. Seed via @Seed annotation - create reference object")
    void first() {
        first = Instancio.of(UUID.class)
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(toSet());

        assertThat(first)
                .as("stream() should generate distinct objects")
                .hasSize(SAMPLE_SIZE);
    }

    @Seed(SEED)
    @Order(2)
    @Test
    @DisplayName("2. Seed via @Seed annotation")
    void second() {
        final Set<UUID> result = Instancio.of(UUID.class)
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(toSet());

        assertThat(result).isEqualTo(first);
    }

    @Order(3)
    @Test
    @DisplayName("3. Seed via API builder")
    void third() {
        final Set<UUID> result = Instancio.of(UUID.class)
                .withSeed(SEED)
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(toSet());

        assertThat(result).isEqualTo(first);
    }

    @Order(4)
    @Test
    @DisplayName("4. Seed via Settings")
    void fourth() {
        final Set<UUID> result = Instancio.of(UUID.class)
                .withSettings(Settings.create().set(Keys.SEED, SEED))
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(toSet());

        assertThat(result).isEqualTo(first);
    }
}
