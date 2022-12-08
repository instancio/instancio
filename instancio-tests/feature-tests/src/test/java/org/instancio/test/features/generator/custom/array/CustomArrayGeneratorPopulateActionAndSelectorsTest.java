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
package org.instancio.test.features.generator.custom.array;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.test.support.pojo.arrays.ArrayOfStringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields_;
import org.instancio.test.support.pojo.misc.StringFields_;
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
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.METAMODEL,
        Feature.MODEL,
        Feature.POPULATE_ACTION,
        Feature.SELECTOR
})
class CustomArrayGeneratorPopulateActionAndSelectorsTest {
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

    private ArrayOfStringAndPrimitiveFields createWith(final PopulateAction action) {
        final Hints hints = Hints.builder().populateAction(action)
                .with(ArrayHint.builder().build())
                .build();

        return Instancio.create(createBaseModelWith(hints));
    }

    private ArrayOfStringAndPrimitiveFields createWithHintsAndSelectors(final PopulateAction action) {
        final Hints hints = Hints.builder().populateAction(action)
                .with(ArrayHint.builder().build())
                .build();

        return Instancio.of(createBaseModelWith(hints))
                .set(StringFields_.one, STR_ONE_OVERRIDE)
                .set(StringFields_.two, STR_TWO_OVERRIDE)
                .set(StringAndPrimitiveFields_.intOne, INT_ONE_OVERRIDE)
                .set(StringAndPrimitiveFields_.intTwo, INT_TWO_OVERRIDE)
                .lenient() // only needed for testing selectors when action is NONE
                .create();
    }

    @Nested
    class WithoutSelectorsTest {

        @Test
        @DisplayName("Action: NONE")
        void populateActionNone() {
            final ArrayOfStringAndPrimitiveFields result = createWith(PopulateAction.NONE);

            assertThat(result.getArray()).contains(existingElement);
            assertExistingElementWasNotModified(result.getArray());
            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: APPLY_SELECTORS")
        void populateActionApplySelectors() {
            final ArrayOfStringAndPrimitiveFields result = createWith(PopulateAction.APPLY_SELECTORS);

            assertThat(result.getArray()).contains(existingElement);
            assertExistingElementWasNotModified(result.getArray());
            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: NULLS")
        void populateActionNulls() {
            final ArrayOfStringAndPrimitiveFields result = createWith(PopulateAction.NULLS);

            assertThat(result.getArray()).contains(existingElement);

            final StringAndPrimitiveFields existing = result.getArray()[OCCUPIED_INDEX];
            assertThat(existing.getOne()).isEqualTo(EXISTING_STR_ONE);
            assertThat(existing.getIntOne()).isEqualTo(EXISTING_INT_ONE);
            assertNoNulls(existing.getTwo(), existing.getThree(), existing.getFour());
            assertAllZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: NULLS_AND_DEFAULT_PRIMITIVES")
        void populateActionNullsAndDefaultPrimitives() {
            final ArrayOfStringAndPrimitiveFields result = createWith(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getArray()).contains(existingElement);

            final StringAndPrimitiveFields existing = result.getArray()[OCCUPIED_INDEX];
            assertThat(existing.getOne()).isEqualTo(EXISTING_STR_ONE);
            assertThat(existing.getIntOne()).isEqualTo(EXISTING_INT_ONE);
            assertNoNulls(existing.getTwo(), existing.getThree(), existing.getFour());
            assertNoZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: ALL")
        void populateActionAll() {
            final ArrayOfStringAndPrimitiveFields result = createWith(PopulateAction.ALL);

            assertThat(result.getArray())
                    .as("Object replaced with a new instance (!?)")
                    .doesNotContain(existingElement);

            final StringAndPrimitiveFields existing = result.getArray()[OCCUPIED_INDEX];
            assertThat(existing.getOne()).as("overwritten").isNotEqualTo(EXISTING_STR_ONE);
            assertThat(existing.getIntOne()).as("overwritten").isNotEqualTo(EXISTING_INT_ONE);
            assertNoNulls(existing.getTwo(), existing.getThree(), existing.getFour());
            assertNoZeroes(existing.getIntTwo(), existing.getIntThree(), existing.getIntFour());

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }
    }

    @Nested
    class WithSelectorsTest {
        @Test
        @DisplayName("Action: NONE")
        void populateActionNone() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(PopulateAction.NONE);

            assertThat(result.getArray()).contains(existingElement);
            assertExistingElementWasNotModified(result.getArray());
            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: APPLY_SELECTORS")
        void populateActionApplySelectors() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(PopulateAction.APPLY_SELECTORS);

            assertThat(result.getArray())
                    .contains(existingElement)
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertSelectorOverrides(existingElement);
            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: NULLS")
        void populateActionNulls() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(PopulateAction.NULLS);

            assertThat(result.getArray())
                    .contains(existingElement)
                    .allSatisfy(CustomArrayGeneratorPopulateActionAndSelectorsTest::assertSelectorOverrides);

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getArray())
                    .filteredOn(ELEMENT_TO_BE_GENERATED_BY_ENGINE)
                    .allSatisfy(CustomArrayGeneratorPopulateActionAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: NULLS_AND_DEFAULT_PRIMITIVES")
        void populateActionNullsAndDefaultPrimitives() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);

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
                    .allSatisfy(CustomArrayGeneratorPopulateActionAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("Action: ALL")
        void populateActionAll() {
            final ArrayOfStringAndPrimitiveFields result = createWithHintsAndSelectors(PopulateAction.ALL);

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
                    .allSatisfy(CustomArrayGeneratorPopulateActionAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }
    }

    private void assertNullIndicesNotPopulated(final StringAndPrimitiveFields[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i != OCCUPIED_INDEX) {
                assertThat(array[i]).as("Expected index %s to contain null", i).isNull();
            }
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

    // Elements that were created and added to the array by the engine
    // are always fully-populated (regardless of the PopulateAction)
    private static void assertEngineGeneratedElementsAreFullyPopulated(final ArrayOfStringAndPrimitiveFields result) {
        final StringAndPrimitiveFields[] array = result.getArray();
        for (int i = 0; i < array.length; i++) {
            if (i != OCCUPIED_INDEX) {
                final StringAndPrimitiveFields e = array[i];
                assertThatObject(e)
                        .as("String fields should not be null")
                        .hasNoNullFieldsOrProperties();

                assertNoZeroes(
                        e.getIntOne(),
                        e.getIntTwo(),
                        e.getIntThree(),
                        e.getIntFour());

            }
        }
    }
}
