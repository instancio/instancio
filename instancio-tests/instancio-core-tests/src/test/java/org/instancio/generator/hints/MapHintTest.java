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
package org.instancio.generator.hints;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MapHintTest {

    @Test
    void emptyHint() {
        final MapHint empty = MapHint.empty();
        assertThat(empty.generateEntries()).isZero();
        assertThat(empty.nullableMapKeys()).isFalse();
        assertThat(empty.nullableMapValues()).isFalse();
        assertThat(empty.withKeys()).isEmpty();
        assertThat(empty.withEntries()).isEmpty();
    }

    @Test
    void withEntries() {
        final Map<String, String> entries1 = Instancio.ofMap(String.class, String.class).create();
        final Map<String, String> entries2 = Instancio.ofMap(String.class, String.class).create();

        final MapHint hint = MapHint.builder()
                .withEntries(entries1)
                .withEntries(null)
                .withEntries(Collections.emptyMap())
                .withEntries(entries2)
                .build();

        assertThat(hint.withEntries())
                .containsAllEntriesOf(entries1)
                .containsAllEntriesOf(entries2)
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }

    @Test
    void withEKeys() {
        final List<String> keys1 = Instancio.ofList(String.class).create();
        final List<String> keys2 = Instancio.ofList(String.class).create();

        final MapHint hint = MapHint.builder()
                .withKeys(keys1)
                .withKeys(null)
                .withKeys(Collections.emptyList())
                .withKeys(keys2)
                .build();

        assertThat(hint.withKeys())
                .containsAll(keys1)
                .containsAll(keys2)
                .doesNotContainNull();
    }

    @Test
    void verifyToString() {
        assertThat(MapHint.builder().build())
                .hasToString("MapHint[generateEntries=0," +
                        " nullableMapKeys=false," +
                        " nullableMapValues=false," +
                        " withEntries={}]");

        assertThat(MapHint.builder()
                .generateEntries(3)
                .nullableMapKeys(true)
                .nullableMapValues(true)
                .withEntries(Map.of("foo", "bar"))
                .build())
                .hasToString("MapHint[generateEntries=3," +
                        " nullableMapKeys=true," +
                        " nullableMapValues=true," +
                        " withEntries={foo=bar}]");
    }
}
