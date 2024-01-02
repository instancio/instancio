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

package org.instancio.test.guava;

import com.google.common.collect.ImmutableMap;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

/**
 * A {@code HashMap} or {@code ImmutableMap} should have the same contents
 * for a given seed value.
 *
 * <p>This is a nice-to-have and is not guaranteed in future versions.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class CollectionAndGuavaCollectionRepeatabilityTest {

    private static final long SEED = 12345L;

    private static Map<String, Integer> first;

    @Seed(SEED)
    @Order(1)
    @Test
    void first() {
        first = Instancio.of(new TypeToken<Map<String, Integer>>() {})
                .subtype(all(Map.class), HashMap.class)
                .create();

        assertThat(first).isNotEmpty();
    }

    @Seed(SEED)
    @Order(2)
    @Test
    void second() {
        final Map<String, Integer> second = Instancio.of(new TypeToken<Map<String, Integer>>() {})
                .subtype(all(Map.class), ImmutableMap.class)
                .create();

        assertThat(second).isEqualTo(first);
    }
}
