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
package org.instancio.test.features.generator.custom.collection;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.test.support.pojo.collections.lists.ListOfStringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields_;
import org.instancio.test.support.pojo.misc.StringFields_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
        Feature.METAMODEL,
        Feature.MODEL,
        Feature.SELECTOR
})
class CustomCollectionGeneratorAfterGenerateAndSelectorsTest {
    private static final int INITIAL_SIZE = 1;
    private static final int GENERATE_ELEMENTS_HINT = 5;

    private static final int EXISTING_INT_ONE = -1;
    private static final int INT_ONE_OVERRIDE = -100;
    private static final int INT_TWO_OVERRIDE = -200;

    private static final String EXISTING_STR_ONE = "one-original";
    private static final String STR_ONE_OVERRIDE = "one-override";
    private static final String STR_TWO_OVERRIDE = "two-override";

    private final StringAndPrimitiveFields existingElement = StringAndPrimitiveFields.builder()
            .one(EXISTING_STR_ONE)
            .intOne(EXISTING_INT_ONE)
            .build();

    private final Predicate<StringAndPrimitiveFields> ELEMENT_CREATED_BY_USER = e -> e == existingElement;
    private final Predicate<StringAndPrimitiveFields> ELEMENT_GENERATED_BY_ENGINE = ELEMENT_CREATED_BY_USER.negate();

    private class CustomListGenerator implements Generator<List<StringAndPrimitiveFields>> {
        private final Hints hints;

        private CustomListGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public List<StringAndPrimitiveFields> generate(final Random random) {
            final List<StringAndPrimitiveFields> list = new ArrayList<>();
            list.add(existingElement);
            return list;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    private Model<ListOfStringAndPrimitiveFields> createBaseModelWith(final Hints hints) {
        return Instancio.of(ListOfStringAndPrimitiveFields.class)
                .supply(field("list"), new CustomListGenerator(hints))
                .toModel();
    }

    private ListOfStringAndPrimitiveFields createWith(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS_HINT).build())
                .build();

        return Instancio.create(createBaseModelWith(hints));
    }

    private ListOfStringAndPrimitiveFields createWithHintsAndSelectors(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS_HINT).build())
                .build();

        return Instancio.of(createBaseModelWith(hints))
                .set(StringFields_.one, STR_ONE_OVERRIDE)
                .set(StringFields_.two, STR_TWO_OVERRIDE)
                .set(StringAndPrimitiveFields_.intOne, INT_ONE_OVERRIDE)
                .set(StringAndPrimitiveFields_.intTwo, INT_TWO_OVERRIDE)
                .lenient() // only needed for testing selectors when AfterGenerate is DO_NOT_MODIFY
                .create();
    }

    @Nested
    class WithoutSelectorsTest {

        @Test
        @DisplayName("DO_NOT_MODIFY")
        void doNotModify() {
            final ListOfStringAndPrimitiveFields result = createWith(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getList())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertExistingElementWasNotModified);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("APPLY_SELECTORS")
        void applySelectors() {
            final ListOfStringAndPrimitiveFields result = createWith(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getList())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertExistingElementWasNotModified);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS")
        void populateNulls() {
            final ListOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getList())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertThat(e.getOne()).isEqualTo(EXISTING_STR_ONE);
                        assertThat(e.getIntOne()).isEqualTo(EXISTING_INT_ONE);
                        assertNoNulls(e.getTwo(), e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntTwo(), e.getIntThree(), e.getIntFour());
                    });

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES")
        void populateNullsAndDefaultPrimitives() {
            final ListOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getList())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertThat(e.getOne()).isEqualTo(EXISTING_STR_ONE);
                        assertThat(e.getIntOne()).isEqualTo(EXISTING_INT_ONE);
                        assertNoNulls(e.getTwo(), e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntTwo(), e.getIntThree(), e.getIntFour());
                    });

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_ALL")
        void populateAll() {
            final ListOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_ALL);

            assertThat(result.getList())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    // Should be overwritten
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE)
                    .allSatisfy(e -> {
                        // All populated
                        assertNoNulls(e.getOne(), e.getTwo(), e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntOne(), e.getIntTwo(), e.getIntThree(), e.getIntFour());
                    });
        }
    }

    @Nested
    class WithSelectorsTest {
        @Test
        @DisplayName("DO_NOT_MODIFY")
        void doNotModify() {
            final ListOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getList())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    .anyMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .anyMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .hasSize(1)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertExistingElementWasNotModified);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        /**
         * Selectors should be applied to user-created AND engine-created objects.
         */
        @Test
        @DisplayName("APPLY_SELECTORS")
        void applySelectors() {
            final ListOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getList())
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertAllNulls(e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getList())
                    .filteredOn(ELEMENT_GENERATED_BY_ENGINE)
                    .hasSize(GENERATE_ELEMENTS_HINT)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS")
        void populateNulls() {
            final ListOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getList())
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getList())
                    .filteredOn(ELEMENT_GENERATED_BY_ENGINE)
                    .hasSize(GENERATE_ELEMENTS_HINT)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("NULLS_AND_DEFAULT_PRIMITIVES")
        void populateNullsAndDefaultPrimitives() {
            final ListOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getList())
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getList())
                    .filteredOn(ELEMENT_GENERATED_BY_ENGINE)
                    .hasSize(GENERATE_ELEMENTS_HINT)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_ALL")
        void populateAll() {
            final ListOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_ALL);

            assertThat(result.getList())
                    .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS_HINT)
                    .contains(existingElement)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getList())
                    .filteredOn(ELEMENT_CREATED_BY_USER)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getList())
                    .filteredOn(ELEMENT_GENERATED_BY_ENGINE)
                    .hasSize(GENERATE_ELEMENTS_HINT)
                    .allSatisfy(CustomCollectionGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }
    }

    private static void assertExistingElementWasNotModified(final StringAndPrimitiveFields existing) {
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

    private void assertEngineGeneratedElementsAreFullyPopulated(final ListOfStringAndPrimitiveFields result) {
        // Elements that were created and added to the collection by the engine
        // are always fully-populated (regardless of the AfterGenerate value)
        assertThat(result.getList())
                .filteredOn(ELEMENT_GENERATED_BY_ENGINE) // exclude user-created object
                .hasSize(GENERATE_ELEMENTS_HINT)
                .allSatisfy(e -> {
                    assertThatObject(e)
                            .as("String fields should not be null")
                            .hasNoNullFieldsOrProperties();

                    assertNoZeroes(
                            e.getIntOne(),
                            e.getIntTwo(),
                            e.getIntThree(),
                            e.getIntFour());
                });
    }
}
