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
package org.instancio.test.features.generator;

import org.apache.commons.lang3.RandomUtils;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemString;
import org.instancio.test.support.pojo.arrays.primitive.WithIntArray;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag(Feature.GENERATE)
class BuiltInArrayGeneratorTest {
    private static final int EXPECTED_LENGTH = RandomUtils.nextInt(0, 10);

    @Nested
    class UsingOfClassAPITest {

        @Test
        @DisplayName("Array of the target field should have expected size and be fully populated")
        void arrayShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class)
                    .generate(field("array1"), gen -> gen.array().length(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All arrays should have expected size and be fully populated")
        void allArraysShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class)
                    .generate(all(Item[].class), gen -> gen.array().length(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), EXPECTED_LENGTH, EXPECTED_LENGTH);
        }

        @Test
        void withElements() {
            final Integer[] expectedElements = {44, 55};
            final WithIntArray result = Instancio.of(WithIntArray.class)
                    .generate(field("values"), gen -> gen.array().with(expectedElements))
                    .create();

            assertThat(result.getValues()).contains(expectedElements);
        }
    }

    @Nested
    class UsingOfTypeTokenAPITest {

        @Test
        @DisplayName("Array of the target field should have expected size and be fully populated")
        void arrayShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(new TypeToken<TwoArraysOfItemString>() {})
                    .generate(field("array1"), gen -> gen.array().length(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All arrays should have expected size and be fully populated")
        void allArraysShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(new TypeToken<TwoArraysOfItemString>() {})
                    .generate(all(Item[].class), gen -> gen.array().length(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), EXPECTED_LENGTH, EXPECTED_LENGTH);
        }
    }

    @Nested
    class UsingOfModelAPITest {

        @Test
        @DisplayName("Array of the target field should have expected size and be fully populated")
        void arrayShouldHaveExpectedSize() {
            final Model<TwoArraysOfItemString> model = Instancio.of(TwoArraysOfItemString.class)
                    .generate(field("array1"), gen -> gen.array().length(EXPECTED_LENGTH))
                    .toModel();

            final TwoArraysOfItemString result = Instancio.of(model).create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All arrays should have expected size and be fully populated")
        void allArraysShouldHaveExpectedSize() {
            final Model<TwoArraysOfItemString> model = Instancio.of(TwoArraysOfItemString.class)
                    .generate(all(Item[].class), gen -> gen.array().length(EXPECTED_LENGTH))
                    .toModel();

            final TwoArraysOfItemString result = Instancio.of(model).create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), EXPECTED_LENGTH, EXPECTED_LENGTH);
        }
    }

    private static void assertArray(final Item<String>[] array, final int minLength, final int maxLength) {
        assertThat(array).hasSizeBetween(minLength, maxLength)
                .allSatisfy(item -> assertThat(item.getValue()).isInstanceOf(String.class));
    }

}