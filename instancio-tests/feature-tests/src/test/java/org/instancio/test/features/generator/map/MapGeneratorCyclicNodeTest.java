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
package org.instancio.test.features.generator.map;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.internal.util.SystemProperties;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.Asserts.assertNoExceptionWithFailOnErrorEnabled;

@FeatureTag(Feature.CYCLIC)
@ExtendWith(InstancioExtension.class)
class MapGeneratorCyclicNodeTest {

    //@formatter:off
    // Use case: map with cyclic key
    private static @Data class RootCyclicKey { ACyclicKey a; }
    private static @Data class ACyclicKey { BCyclicKey b; }
    private static @Data class BCyclicKey { Map<ACyclicKey, UUID> map; }

    // Use case: map with cyclic value
    private static @Data class RootCyclicValue { ACyclicValue a; }
    private static @Data class ACyclicValue { BCyclicValue b; }
    private static @Data class BCyclicValue { Map<UUID, ACyclicValue> map; }
    //@formatter:on

    @Nested
    class CyclicKeyTest {

        @Test
        void create() {
            final RootCyclicKey result = Instancio.create(RootCyclicKey.class);

            assertThat(result.a.b.map).isEmpty();
        }

        /**
         * No exception should be thrown when {@link SystemProperties#FAIL_ON_ERROR} is enabled.
         */
        @Test
        void withEntry() {
            final ACyclicKey expectedKey = new ACyclicKey();
            final UUID expectedValue = Instancio.create(UUID.class);

            final InstancioApi<RootCyclicKey> api = Instancio.of(RootCyclicKey.class)
                    .generate(field(BCyclicKey::getMap), gen -> gen.map()
                            .size(100) // won't be able to generate but should not throw an error
                            .with(expectedKey, expectedValue));

            final RootCyclicKey result = assertNoExceptionWithFailOnErrorEnabled(api::create);

            assertThat(result.a.b.map)
                    .hasSize(1)
                    .containsOnlyKeys(expectedKey)
                    .containsValue(expectedValue);
        }
    }

    @Nested
    class CyclicValueTest {

        @Test
        void create() {
            final RootCyclicValue result = Instancio.create(RootCyclicValue.class);

            assertThat(result.a.b.map).isEmpty();
        }

        /**
         * No exception should be thrown when {@link SystemProperties#FAIL_ON_ERROR} is enabled.
         */
        @Test
        void withEntry() {
            final UUID expectedKey = Instancio.create(UUID.class);
            final ACyclicValue expectedValue = new ACyclicValue();

            final InstancioApi<RootCyclicValue> api = Instancio.of(RootCyclicValue.class)
                    .generate(field(BCyclicValue::getMap), gen -> gen.map()
                            .size(100) // won't be able to generate but should not throw an error
                            .with(expectedKey, expectedValue));

            final RootCyclicValue result = assertNoExceptionWithFailOnErrorEnabled(api::create);

            assertThat(result.a.b.map)
                    .hasSize(1)
                    .containsOnlyKeys(expectedKey)
                    .containsValue(expectedValue);
        }
    }
}