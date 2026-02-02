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
package org.instancio.test.java21;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.SequencedMap;
import java.util.TreeMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class Java21SupportedMapsTest {

    @Test
    void verifyCreate() {
        final Class<?> expectedSubtype = TreeMap.class;
        final Map<UUID, String> result = Instancio.create(new TypeToken<SequencedMap<UUID, String>>() {});

        assertThat(result)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .isInstanceOf(expectedSubtype);
    }

    @Test
    void verifyCreateWithSize() {
        final int size = 5;
        final Class<?> expectedSubtype = TreeMap.class;
        final UUID expectedKey = Instancio.create(UUID.class);
        final String expectedValue = Instancio.create(String.class);
        final Map<UUID, String> result = Instancio.of(new TypeToken<SequencedMap<UUID, String>>() {})
                .generate(root(), gen -> gen.map().size(size).with(expectedKey, expectedValue))
                .create();

        assertThat(result)
                .hasSize(size + 1) // plus expected entry
                .containsEntry(expectedKey, expectedValue)
                .isInstanceOf(expectedSubtype);
    }
}
