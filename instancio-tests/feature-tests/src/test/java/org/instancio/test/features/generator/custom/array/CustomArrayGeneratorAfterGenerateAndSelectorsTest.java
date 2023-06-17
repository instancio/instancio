/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.generator.custom.array;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.test.support.pojo.arrays.ArrayOfStringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.Asserts.assertAllNulls;
import static org.instancio.test.support.asserts.Asserts.assertAllZeroes;
import static org.instancio.test.support.asserts.Asserts.assertNoNulls;
import static org.instancio.test.support.asserts.Asserts.assertNoZeroes;

@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.MODEL,
        Feature.SELECTOR
})
class CustomArrayGeneratorAfterGenerateAndSelectorsTest {
    private static final int INITIAL_SIZE = 5;
    private static final int OCCUPIED_INDEX = 2;

    private static final int EXISTING_INT_ONE = 1;
    private static final int INT_ONE_OVERRIDE = -1;
    private static final int INT_TWO_OVERRIDE = -2;

    private static final String EXISTING_STR_ONE = "one-original";
    private static final String STR_ONE_OVERRIDE = "one-override";
    private static final String STR_TWO_OVERRIDE = "two-override";

    private final StringAndPrimitiveFields existingElement = StringAndPrimitiveFields.builder()
            .one(EXISTING_STR_ONE)
            .intOne(EXISTING_INT_ONE)
            .build();

    private final Predicate<StringAndPrimitiveFields> ELEMENT_CREATED_BY_USER = e -> e == existingElement;
    private final Predicate<StringAndPrimitiveFields> ELEMENT_TO_BE_GENERATED_BY_ENGINE = ELEMENT_CREATED_BY_USER.negate();

    private class CustomArrayGenerator implements Generator<StringAndPrimitiveFields[]> {
        private final Hints hints;

        private CustomArrayGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public StringAndPrimitiveFields[] generate(final Random random) {
            final StringAndPrimitiveFields[] array = new StringAndPrimitiveFields[INITIAL_SIZE];
            array[OCCUPIED_INDEX] = existingElement;
            return array;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    private Model<ArrayOfStringAndPrimitiveFields> createBaseModelWith(final Hints hints) {
        return Instancio.of(ArrayOfStringAndPrimitiveFields.class)
                .supply(field("array"), new CustomArrayGenerator(hints))
                .toModel();
    }

    private ArrayOfStringAndPrimitiveFields createWith(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(ArrayHint.builder().build())
                .build();

        return Instancio.create(createBaseModelWith(hints));
    }

    private ArrayOfStringAndPrimitiveFields createWithHintsAndSelectors(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(ArrayHint.builder().build())
                .build();

        return Instancio.of(createBaseModelWith(hints))
                .set(field(StringFields::getOne), STR_ONE_OVERRIDE)
                .set(field(StringFields::getTwo), STR_TWO_OVERRIDE)
                .set(field(StringAndPrimitiveFields::getIntOne), INT_ONE_OVERRIDE)
                .set(field(StringAndPrimitiveFields::getIntTwo), INT_TWO_OVERRIDE)
                .lenient() // only needed for testing selectors when AfterGenerate is DO_NOT_MODIFY
                .create();
    }

    @Nested
    class WithoutSelectorsTest {

        @Test
        @DisplayName("DO_NOT_MODIFY")
        void doNotModify() {
            final ArrayOfStringAndPrimitiveFields result = createWith(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getArray()).contains(existingElement);
            assertExistingElementWasNotModified(result.getArray());
            assertEmptyIndexesAreNotPopulated(result);
        }

        @Test
        @DisplayName("APPLY_SELECTORS")
        void applySelectors() {
            final ArrayOfStringAndPrimitiveFields result = createWith(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getArray()).contains(existingElement);
            assertExistingElementWasNotModified(result.getArray());
            assertEmptyIndexesAreNotPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS")
        void populateNulls() {
            final ArrayOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getArray()).contains(existingElement);

            final StringAndPrimitiveFields existing = result.getArray()[OCCUPIED_INDEX];
            assertThat(existing.getOne()).isEqualTo(EXISTING_STR_ONE);
            assertThat(existing.getIntOne()).isEqualTo(EXISTING_INT_ONE);
            assertNoNulls(existing.getTwo(), existing.getThree(), existing.getFour());
            assertAllZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());

            assertEmptyIndexesAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES")
        void populateNullsAndDefaultPrimitives() {
            final ArrayOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getArray()).contains(existingElement);

            final StringAndPrimitiveFields existing = result.getArray()[OCCUPIED_INDEX];
            assertThat(existing.getOne()).isEqualTo(EXISTING_STR_ONE);
            assertThat(existing.getIntOne()).isEqualTo(EXISTING_INT_ONE);
            assertNoNulls(existing.getTwo(), existing.getThree(), existing.getFour());
            assertNoZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());

            assertEmptyIndexesAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_ALL")
        void populateAll() {
            final ArrayOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_ALL);

            assertThat(result.getArray())
                    .as("Object replaced with a new instance (!?)")
                    .doesNotContain(existingElement);

            final StringAndPrimitiveFields existing = result.getArray()[OCCUPIED_INDEX];
            assertThat(existing.getOne()).as("overwritten").isNotEqualTo(EXISTING_STR_ONE);
            assertThat(existing.getIntOne()).as("overwritten").isNotEqualTo(EXISTING_INT_ONE);
            assertNoNulls(existing.getTwo(), existing.getThree(), existing.getFour());
            assertNoZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());

            assertEmptyIndexesAreFullyPopulated(result);
        }
    }

    @Nested
    class WithSelectorsTest {

        @Test
        @DisplayName("DO_NOT_MODIFY")
        void doNotModify() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getArray()).contains(existingElement);
            assertExistingElementWasNotModified(result.getArray());
            assertEmptyIndexesAreNotPopulated(result);
        }

        @Test
        @DisplayName("APPLY_SELECTORS")
        void applySelectors() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getArray())
                    .contains(existingElement)
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertSelectorOverrides(existingElement);
            assertEmptyIndexesAreNotPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS")
        void populateNulls() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getArray())
                    .contains(existingElement)
                    .allSatisfy(CustomArrayGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_TO_BE_GENERATED_BY_ENGINE)
                    .allSatisfy(CustomArrayGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEmptyIndexesAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES")
        void populateNullsAndDefaultPrimitives() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getArray())
                    .contains(existingElement)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_TO_BE_GENERATED_BY_ENGINE)
                    .allSatisfy(CustomArrayGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEmptyIndexesAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_ALL")
        void populateAll() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_ALL);

            assertThat(result.getArray())
                    .doesNotContain(existingElement)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_TO_BE_GENERATED_BY_ENGINE)
                    .allSatisfy(CustomArrayGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEmptyIndexesAreFullyPopulated(result);
        }
    }

    private static void assertExistingElementWasNotModified(final StringAndPrimitiveFields[] array) {
        final StringAndPrimitiveFields existing = array[OCCUPIED_INDEX];
        assertThat(existing.getOne()).isEqualTo(EXISTING_STR_ONE);
        assertThat(existing.getIntOne()).isEqualTo(EXISTING_INT_ONE);
        assertAllNulls(existing.getTwo(), existing.getThree(), existing.getFour());
        assertAllZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());
    }

    private static void assertSelectorOverrides(final StringAndPrimitiveFields obj) {
        assertThat(obj.getOne()).isEqualTo(STR_ONE_OVERRIDE);
        assertThat(obj.getTwo()).isEqualTo(STR_TWO_OVERRIDE);
        assertThat(obj.getIntOne()).isEqualTo(INT_ONE_OVERRIDE);
        assertThat(obj.getIntTwo()).isEqualTo(INT_TWO_OVERRIDE);
    }

    private static void assertEmptyIndexesAreNotPopulated(final ArrayOfStringAndPrimitiveFields result) {
        final StringAndPrimitiveFields[] array = result.getArray();
        for (int i = 0; i < array.length; i++) {
            if (i != OCCUPIED_INDEX) {
                assertThatObject(array[i]).isNull();
            }
        }
    }

    private static void assertEmptyIndexesAreFullyPopulated(final ArrayOfStringAndPrimitiveFields result) {
        final StringAndPrimitiveFields[] array = result.getArray();
        for (int i = 0; i < array.length; i++) {
            if (i != OCCUPIED_INDEX) {
                final StringAndPrimitiveFields e = array[i];
                assertThatObject(e).hasNoNullFieldsOrProperties();

                assertNoZeroes(
                        e.getIntOne(),
                        e.getIntTwo(),
                        e.getIntThree(),
                        e.getIntFour());
            }
        }
    }
}
