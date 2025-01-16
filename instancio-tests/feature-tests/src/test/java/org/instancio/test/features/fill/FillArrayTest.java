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
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.settings.Keys;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.FILL)
@ExtendWith(InstancioExtension.class)
class FillArrayTest {

    private static @Data class StringsAbcArray {
        private StringsAbc[] array;
    }

    @EnumSource(value = FillType.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldPopulateArrayOfPojos(final FillType fillType) {
        final StringsAbc[] array = new StringsAbc[2];
        final StringsAbcArray object = new StringsAbcArray();
        object.array = array;
        object.array[0] = StringsAbc.builder().a("A").build();
        object.array[1] = StringsAbc.builder().b("B").build();

        Instancio.ofObject(object)
                .withSetting(Keys.FILL_TYPE, fillType)
                .set(field(StringsGhi::getH), "H")
                .fill();

        assertThat(object.array)
                .isSameAs(array)
                .hasSize(2);

        assertThat(object.array[0])
                .satisfies(element -> {
                    assertThat(element.getA()).isEqualTo("A");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThat(object.array[1])
                .satisfies(element -> {
                    assertThat(element.getB()).isEqualTo("B");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThatObject(object).isFullyPopulated();
    }

    @EnumSource(FillType.class)
    @ParameterizedTest
    void modifyArrayElementViaSelector(final FillType fillType) {
        final String[] object = {"foo", null};

        Instancio.ofObject(object)
                .withFillType(fillType)
                .set(allStrings(), "bar")
                .fill();

        assertThat(object).containsOnly("bar");
    }

    @EnumSource(value = FillType.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void populateNullElements(final FillType fillType) {
        final String[] object = {"foo", null};

        Instancio.ofObject(object)
                .withFillType(fillType)
                .fill();

        assertThat(object[0]).isEqualTo("foo");
        assertThat(object[1]).is(Conditions.RANDOM_STRING);
    }

    @Test
    void shouldNotModifyArrayElements() {
        final String[] object = {"foo", null};

        Instancio.ofObject(object)
                .withFillType(FillType.APPLY_SELECTORS)
                .fill();

        assertThat(object).containsExactly("foo", null);
    }

    @Nested
    class FillWithPrimitiveArrayTest {

        @EnumSource(value = FillType.class, names = {"APPLY_SELECTORS", "POPULATE_NULLS"})
        @ParameterizedTest
        void shouldNotModifyPrimitiveArrayElements(final FillType fillType) {
            final int[] object = {-1, 0};

            Instancio.ofObject(object)
                    .withFillType(fillType)
                    .fill();

            assertThat(object).containsExactly(-1, 0);
        }

        @Test
        void shouldPopulatePrimitiveArrayElements() {
            final int[] object = {-1, 0};

            Instancio.fill(object);

            assertThat(object[0]).isEqualTo(-1);
            assertThat(object[1]).isPositive();
        }

        @EnumSource(FillType.class)
        @ParameterizedTest
        void shouldPopulatePrimitiveArrayElementsViaSelector(final FillType fillType) {
            final int[] object = {-1, 0};

            Instancio.ofObject(object)
                    .withFillType(fillType)
                    .set(allInts(), -2)
                    .fill();

            assertThat(object).containsOnly(-2);
        }
    }
}
