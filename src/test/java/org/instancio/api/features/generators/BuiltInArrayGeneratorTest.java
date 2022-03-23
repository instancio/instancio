package org.instancio.api.features.generators;

import org.apache.commons.lang3.RandomUtils;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.pojo.arrays.TwoArraysOfItemString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.field;

class BuiltInArrayGeneratorTest {
    private static final int EXPECTED_LENGTH = RandomUtils.nextInt(0, 10);

    @Nested
    class UsingOfClassAPITest {

        @Test
        @DisplayName("Array of the target field should have expected size and be fully populated")
        void arrayShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class)
                    .generate(field("array1"), gen -> gen.array()
                            .minLength(EXPECTED_LENGTH)
                            .maxLength(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All arrays should have expected size and be fully populated")
        void allArraysShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class)
                    .generate(all(Item[].class), gen -> gen.array()
                            .minLength(EXPECTED_LENGTH)
                            .maxLength(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), EXPECTED_LENGTH, EXPECTED_LENGTH);
        }
    }

    @Nested
    class UsingOfTypeTokenAPITest {

        @Test
        @DisplayName("Array of the target field should have expected size and be fully populated")
        void arrayShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(new TypeToken<TwoArraysOfItemString>() {})
                    .generate(field("array1"), gen -> gen.array()
                            .minLength(EXPECTED_LENGTH)
                            .maxLength(EXPECTED_LENGTH))
                    .create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All arrays should have expected size and be fully populated")
        void allArraysShouldHaveExpectedSize() {
            final TwoArraysOfItemString result = Instancio.of(new TypeToken<TwoArraysOfItemString>() {})
                    .generate(all(Item[].class), gen -> gen.array()
                            .minLength(EXPECTED_LENGTH)
                            .maxLength(EXPECTED_LENGTH))
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
                    .generate(field("array1"), gen -> gen.array()
                            .minLength(EXPECTED_LENGTH)
                            .maxLength(EXPECTED_LENGTH))
                    .toModel();

            final TwoArraysOfItemString result = Instancio.of(model).create();

            assertArray(result.getArray1(), EXPECTED_LENGTH, EXPECTED_LENGTH);
            assertArray(result.getArray2(), Constants.MIN_SIZE, Constants.MAX_SIZE);
        }

        @Test
        @DisplayName("All arrays should have expected size and be fully populated")
        void allArraysShouldHaveExpectedSize() {
            final Model<TwoArraysOfItemString> model = Instancio.of(TwoArraysOfItemString.class)
                    .generate(all(Item[].class), gen -> gen.array()
                            .minLength(EXPECTED_LENGTH)
                            .maxLength(EXPECTED_LENGTH))
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
