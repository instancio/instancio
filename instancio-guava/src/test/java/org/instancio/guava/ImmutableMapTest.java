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
package org.instancio.guava;

import com.google.common.collect.ImmutableMap;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.types;

class ImmutableMapTest {

    private static final int EXPECTED_SIZE = 10;

    private static class Holder {
        Map<String, Long> map;
    }

    @Test
    void immutableMapDefaultSize() {
        assertThat(Instancio.create(new TypeToken<ImmutableMap<String, Long>>() {}).size())
                .isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

    @Test
    void immutableMap() {
        final Map<String, Long> result = Instancio.of(new TypeToken<ImmutableMap<String, Long>>() {})
                .generate(types().of(Map.class), gen -> gen.map().size(EXPECTED_SIZE))
                .create();

        assertImmutableMap(result);
    }

    @Test
    void subtype() {
        final Holder result = Instancio.of(Holder.class)
                .subtype(all(Map.class), ImmutableMap.class)
                .generate(all(Map.class), gen -> gen.map().size(EXPECTED_SIZE))
                .create();

        assertImmutableMap(result.map);
    }

    @Test
    void subtypeUsingGenerator() {
        final Holder result = Instancio.of(Holder.class)
                .generate(all(Map.class), gen -> gen.map().size(EXPECTED_SIZE).subtype(ImmutableMap.class))
                .create();

        assertImmutableMap(result.map);
    }

    private static void assertImmutableMap(final Map<String, Long> result) {
        assertThat(result)
                .isInstanceOf(ImmutableMap.class)
                .hasSize(EXPECTED_SIZE)
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }

}
