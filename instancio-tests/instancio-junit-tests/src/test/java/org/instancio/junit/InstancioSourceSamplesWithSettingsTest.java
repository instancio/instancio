/*
 *  Copyright 2022-2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class InstancioSourceSamplesWithSettingsTest {

    private static final int NUM_SAMPLES = Instancio.gen().ints().range(1, 50).get();

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.INSTANCIO_SOURCE_SAMPLES, NUM_SAMPLES);

    private final Set<UUID> results = new HashSet<>();

    @Order(1)
    @InstancioSource
    @ParameterizedTest
    void first(final UUID uuid) {
        assertThat(uuid).isNotNull();

        results.add(uuid);
    }

    @Order(2)
    @Test
    void second() {
        assertThat(results).hasSize(NUM_SAMPLES);
    }
}
