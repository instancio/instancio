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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.MiscArrays;
import org.instancio.test.support.pojo.arrays.object.WithStringArray;
import org.instancio.test.support.pojo.arrays.primitive.WithIntArray;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorArrayTest {

    private static final int SIZE = 5;
    private static final int LAST_INDEX = SIZE - 1;
    private static final String EXPECTED_STRING = "__value__";

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE);

    @Nested
    class ObjectArrayWholeElement {

        @Test
        void first() {
            final WithStringArray result = Instancio.of(WithStringArray.class)
                    .set(elementOf(WithStringArray::getValues).first(), EXPECTED_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[0]");
        }

        @Test
        void last() {
            final WithStringArray result = Instancio.of(WithStringArray.class)
                    .set(elementOf(WithStringArray::getValues).last(), EXPECTED_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[%s]".formatted(LAST_INDEX));
        }

        @Test
        void atIndices() {
            final WithStringArray result = Instancio.of(WithStringArray.class)
                    .set(elementOf(WithStringArray::getValues).at(0, 2, 4), EXPECTED_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[0,2,4]");
        }

        @Test
        void range() {
            final WithStringArray result = Instancio.of(WithStringArray.class)
                    .set(elementOf(WithStringArray::getValues).range(1, 3), EXPECTED_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[1-3]");
        }

        @Test
        void except() {
            final WithStringArray result = Instancio.of(WithStringArray.class)
                    .set(elementOf(WithStringArray::getValues).except(2), EXPECTED_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[0,1,3,4]");
        }
    }

    @Nested
    class PrimitiveArrayWholeElement {

        @Test
        void intArrayAt() {
            final WithIntArray result = Instancio.of(WithIntArray.class)
                    .set(elementOf(WithIntArray::getValues).at(0), 111)
                    .set(elementOf(WithIntArray::getValues).at(4), 999)
                    .create();

            assertThat(result.getValues()).hasSize(SIZE);
            assertThat(result.getValues()[0]).isEqualTo(111);
            assertThat(result.getValues()[4]).isEqualTo(999);
        }

        @Test
        void primitiveLongArrayFirstAndLast() {
            final MiscArrays result = Instancio.of(MiscArrays.class)
                    .set(elementOf(MiscArrays::getPrimitiveLongArray).first(), 1L)
                    .set(elementOf(MiscArrays::getPrimitiveLongArray).last(), 9L)
                    .create();

            assertThat(result.getPrimitiveLongArray()[0]).isEqualTo(1L);
            assertThat(result.getPrimitiveLongArray()[result.getPrimitiveLongArray().length - 1]).isEqualTo(9L);
        }
    }

    @Nested
    class ObjectArraySubField {

        @Test
        void fieldOnItemArrayElement() {
            final MiscArrays result = Instancio.of(MiscArrays.class)
                    .set(elementOf(MiscArrays::getItemStringArray).at(0, 2).target(field(Item.class, "value")), EXPECTED_STRING)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "itemStringArray[0,2].value");
        }
    }
}
