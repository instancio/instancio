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
package org.instancio.test.features.generator.custom.map;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.MapHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.maps.MapOfStringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.Asserts.assertAllNulls;
import static org.instancio.test.support.asserts.Asserts.assertAllZeroes;
import static org.instancio.test.support.asserts.Asserts.assertNoNulls;
import static org.instancio.test.support.asserts.Asserts.assertNoZeroes;

/**
 * Note: this test does not verify applying selectors to Map keys.
 */
@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.MODEL,
        Feature.SELECTOR
})
@ExtendWith(InstancioExtension.class)
class CustomMapGeneratorAfterGenerateAndSelectorsTest {
    private static final int INITIAL_SIZE = 1;
    private static final int GENERATE_ENTRIES_HINT = 5;

    private static final int EXISTING_INT_ONE = -1;
    private static final int INT_ONE_OVERRIDE = -100;
    private static final int INT_TWO_OVERRIDE = -200;

    private static final String EXISTING_STR_ONE = "one-original";
    private static final String STR_ONE_OVERRIDE = "one-override";
    private static final String STR_TWO_OVERRIDE = "two-override";

    private static final UUID EXISTING_KEY = UUID.randomUUID();

    private final StringAndPrimitiveFields existingEntry = StringAndPrimitiveFields.builder()
            .one(EXISTING_STR_ONE)
            .intOne(EXISTING_INT_ONE)
            .build();

    private final Predicate<Map.Entry<UUID, StringAndPrimitiveFields>> ENTRY_CREATED_BY_USER = e -> e.getValue() == existingEntry;
    private final Predicate<Map.Entry<UUID, StringAndPrimitiveFields>> ENTRY_GENERATED_BY_ENGINE = ENTRY_CREATED_BY_USER.negate();

    private class CustomMapGenerator implements Generator<Map<UUID, StringAndPrimitiveFields>> {
        private final Hints hints;

        private CustomMapGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public Map<UUID, StringAndPrimitiveFields> generate(final Random random) {
            final Map<UUID, StringAndPrimitiveFields> map = new HashMap<>();
            map.put(EXISTING_KEY, existingEntry);
            return map;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    private Model<MapOfStringAndPrimitiveFields> createBaseModelWith(final Hints hints) {
        return Instancio.of(MapOfStringAndPrimitiveFields.class)
                .supply(field("map"), new CustomMapGenerator(hints))
                .toModel();
    }

    private MapOfStringAndPrimitiveFields createWith(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(MapHint.builder().generateEntries(GENERATE_ENTRIES_HINT).build())
                .build();

        return Instancio.create(createBaseModelWith(hints));
    }

    private MapOfStringAndPrimitiveFields createWithHintsAndSelectors(final AfterGenerate afterGenerate) {
        final Hints hints = Hints.builder().afterGenerate(afterGenerate)
                .with(MapHint.builder().generateEntries(GENERATE_ENTRIES_HINT).build())
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
            final MapOfStringAndPrimitiveFields result = createWith(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getMap().entrySet())
                    .as("No elements added")
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertExistingElementWasNotModified);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("APPLY_SELECTORS")
        void applySelectors() {
            final MapOfStringAndPrimitiveFields result = createWith(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getMap().entrySet())
                    .extracting(Map.Entry::getValue)
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .contains(existingEntry);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertExistingElementWasNotModified);

            // Elements generated by the engine are fully-populated by the engine
            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS")
        void populateNulls() {
            final MapOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getMap().entrySet())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(e -> {
                        assertThat(e.getOne()).isEqualTo(EXISTING_STR_ONE);
                        assertThat(e.getIntOne()).isEqualTo(EXISTING_INT_ONE);
                        assertNoNulls(e.getTwo(), e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntTwo(), e.getIntThree(), e.getIntFour());
                    });

            // Elements generated by the engine are fully-populated by the engine
            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES")
        void populateNullsAndDefaultPrimitives() {
            final MapOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getMap().entrySet())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
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
            final MapOfStringAndPrimitiveFields result = createWith(AfterGenerate.POPULATE_ALL);

            assertThat(result.getMap().entrySet())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    // Should be overwritten
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE)
                    .allSatisfy(e -> {
                        // All populated
                        assertNoNulls(e.getOne(), e.getTwo(), e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntOne(), e.getIntTwo(), e.getIntThree(), e.getIntFour());
                    });

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }
    }

    @Nested
    class WithSelectorsTest {
        @Test
        @DisplayName("DO_NOT_MODIFY")
        void doNotModify() {
            final MapOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getMap().entrySet())
                    .doesNotContainNull()
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .anyMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .anyMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .hasSize(1)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertExistingElementWasNotModified);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        /**
         * Selectors should be applied to user-created AND engine-created objects.
         */
        @Test
        @DisplayName("APPLY_SELECTORS")
        void applySelectors() {
            final MapOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getMap().entrySet())
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertAllNulls(e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_GENERATED_BY_ENGINE)
                    .extracting(Map.Entry::getValue)
                    .hasSize(GENERATE_ENTRIES_HINT)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS")
        void populateNulls() {
            final MapOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getMap().entrySet())
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertAllZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_GENERATED_BY_ENGINE)
                    .extracting(Map.Entry::getValue)
                    .hasSize(GENERATE_ENTRIES_HINT)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES")
        void populateNullsAndDefaultPrimitives() {
            final MapOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getMap().entrySet())
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_GENERATED_BY_ENGINE)
                    .extracting(Map.Entry::getValue)
                    .hasSize(GENERATE_ENTRIES_HINT)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

            assertEngineGeneratedElementsAreFullyPopulated(result);
        }

        @Test
        @DisplayName("POPULATE_ALL")
        void populateAll() {
            final MapOfStringAndPrimitiveFields result = createWithHintsAndSelectors(AfterGenerate.POPULATE_ALL);

            assertThat(result.getMap().entrySet())
                    .hasSize(INITIAL_SIZE + GENERATE_ENTRIES_HINT)
                    .extracting(Map.Entry::getValue)
                    .contains(existingEntry)
                    .noneMatch(e -> e.getOne().equals(EXISTING_STR_ONE))
                    .noneMatch(e -> e.getIntOne() == EXISTING_INT_ONE);

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_CREATED_BY_USER)
                    .extracting(Map.Entry::getValue)
                    .allSatisfy(e -> {
                        assertSelectorOverrides(e);
                        assertNoNulls(e.getThree(), e.getFour());
                        assertNoZeroes(e.getIntThree(), e.getIntFour());
                    });

            assertThat(result.getMap().entrySet())
                    .filteredOn(ENTRY_GENERATED_BY_ENGINE)
                    .extracting(Map.Entry::getValue)
                    .hasSize(GENERATE_ENTRIES_HINT)
                    .allSatisfy(CustomMapGeneratorAfterGenerateAndSelectorsTest::assertSelectorOverrides);

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

    private void assertEngineGeneratedElementsAreFullyPopulated(final MapOfStringAndPrimitiveFields result) {
        // Elements that were created and added to the collection by the engine
        // are always fully-populated (regardless of the AfterGenerate value)
        assertThat(result.getMap().entrySet())
                .filteredOn(ENTRY_GENERATED_BY_ENGINE) // exclude user-created object
                .hasSize(GENERATE_ENTRIES_HINT)
                .extracting(Map.Entry::getValue)
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
