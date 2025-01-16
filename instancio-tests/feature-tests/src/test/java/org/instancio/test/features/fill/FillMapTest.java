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
package org.instancio.test.features.fill;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.FILL)
@ExtendWith(InstancioExtension.class)
class FillMapTest {

    private static @Data class StringsAbcWithLinkedHashMap {
        private final Map<StringsAbc, UUID> objectAsKeys = new LinkedHashMap<>();
        private final Map<UUID, StringsAbc> objectAsValues = new LinkedHashMap<>();
    }

    @EnumSource(value = FillType.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldPopulateMapOfPojos(final FillType fillType) {
        final StringsAbcWithLinkedHashMap object = new StringsAbcWithLinkedHashMap();
        object.objectAsKeys.put(StringsAbc.builder().a("A").build(), UUID.randomUUID());
        object.objectAsKeys.put(StringsAbc.builder().b("B").build(), UUID.randomUUID());
        object.objectAsValues.put(UUID.randomUUID(), StringsAbc.builder().a("A").build());
        object.objectAsValues.put(UUID.randomUUID(), StringsAbc.builder().b("B").build());

        Instancio.ofObject(object)
                .withFillType(fillType)
                .set(field(StringsGhi::getH), "H")
                .fill();

        assertThat(object.objectAsKeys.keySet())
                .hasSize(2)
                .first()
                .satisfies(element -> {
                    assertThat(element.getA()).isEqualTo("A");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThat(object.objectAsValues.values())
                .last()
                .satisfies(element -> {
                    assertThat(element.getB()).isEqualTo("B");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThatObject(object).isFullyPopulated();
    }

    @EnumSource(FillType.class)
    @ParameterizedTest
    void shouldNotReplaceExistingMapKeysAndValues(final FillType fillType) {
        final String initialKey = "foo";
        final int initialValue = -1;
        final Map<String, Integer> object = new HashMap<>();
        object.put(initialKey, initialValue);

        Instancio.ofObject(object)
                .withFillType(fillType)
                .set(allStrings().lenient(), "bar")
                .fill();

        assertThat(object).containsOnlyKeys(initialKey);
        assertThat(object.values()).first().isEqualTo(initialValue);
    }

    @Test
    void mapOfPojosWithNullKeys() {
        final Map<StringHolder, Integer> object = new LinkedHashMap<>();
        object.put(null, null);
        object.put(new StringHolder("A"), null);
        object.put(new StringHolder("A"), null);
        object.put(null, 1);

        Instancio.fill(object);

        assertThat(object.keySet())
                .extracting(holder -> holder == null ? null : holder.getValue())
                .containsExactly(null, "A");

        assertThat(object.values())
                .containsExactly(1, null);
    }

    @Nested
    class MapWithNullsTest {

        @Test
        void mapWithOnlyNullKeyValue() {
            final Map<Integer, StringHolder> object = new HashMap<>();
            object.put(null, null);

            assertThatThrownBy(() -> Instancio.fill(object))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("the fill() method cannot resolve type arguments" +
                            " from the given HashMap instance because it contains null value(s)");
        }

        @Test
        void mapWithNullKeyAndNonNullValue() {
            final Map<String, Integer> object = new LinkedHashMap<>();
            object.put(null, 42);
            object.put("test", null);

            Instancio.fill(object);

            assertThat(object)
                    .hasSize(2)
                    .containsEntry(null, 42)
                    .containsEntry("test", null);
        }

        @Test
        void mapWithNonNullKeyAndNullValue() {
            final Map<String, Integer> object = new LinkedHashMap<>();
            object.put("test", null);
            object.put(null, 42);

            Instancio.fill(object);

            assertThat(object)
                    .hasSize(2)
                    .containsEntry(null, 42)
                    .containsEntry("test", null);
        }

        @Test
        void mapWithMixedEntries() {
            final Map<String, Integer> object = new LinkedHashMap<>();
            object.put(null, null);
            object.put("test", 42);

            Instancio.fill(object);

            assertThat(object)
                    .hasSize(2)
                    .containsEntry(null, null)
                    .containsEntry("test", 42);
        }
    }
}
