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
package org.instancio.test.java17.generator;

import org.assertj.core.api.ThrowingConsumer;
import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.test.support.conditions.Conditions.RANDOM_INTEGER;
import static org.instancio.test.support.conditions.Conditions.RANDOM_STRING;

@ExtendWith(InstancioExtension.class)
class CustomerGeneratorRecordTest {

    /**
     * Field names starting with "_initial" prefix should be added to {@code ignore()}
     * to ensure they are not modified by Instancio. These fields are used as a reference
     * for verifying a corresponding initialised field has not been reassigned.
     */
    private static final int INITIAL_INT = -100;
    private static final int SELECTOR_INT = -200;
    private static final String INITIAL_STRING = "initial";
    private static final String SELECTOR_STRING = "overwrite";

    private static final ThrowingConsumer<Pojo> SHOULD_BE_RANDOM_POJO = r -> {
        assertThat(r.n1).is(RANDOM_INTEGER);
        assertThat(r.n2).is(RANDOM_INTEGER);
        assertThat(r.s1).is(RANDOM_STRING);
        assertThat(r.s2).is(RANDOM_STRING);
    };
    private static final ThrowingConsumer<Pojo> SHOULD_BE_OVERWRITTEN_BY_SELECTOR = r -> {
        assertThat(r.n1).isEqualTo(SELECTOR_INT);
        assertThat(r.n2).isEqualTo(SELECTOR_INT);
        assertThat(r.s1).isEqualTo(SELECTOR_STRING);
        assertThat(r.s2).isEqualTo(SELECTOR_STRING);
    };
    private static final ThrowingConsumer<Pojo> SHOULD_BE_UNMODIFIED = r -> {
        assertThat(r.n1).isZero();
        assertThat(r.n2).isEqualTo(INITIAL_INT);
        assertThat(r.s1).isNull();
        assertThat(r.s2).isEqualTo(INITIAL_STRING);
    };


    private static class Pojo {
        private String s1;
        private String s2 = INITIAL_STRING;
        private int n1;
        private int n2 = INITIAL_INT;
    }

    private record Container(Pojo pojo) {}

    private Model<Container> model(final boolean overwriteExistingValues, final AfterGenerate afterGenerate) {
        // Default AfterGenerate setting shouldn't matter since the Generator hint should take precedence.
        // Using a random value to ensure this is correct.
        final AfterGenerate defaultAfterGenerate = Gen.enumOf(AfterGenerate.class).get();

        return Instancio.of(Container.class)
                .withSettings(Settings.create()
                        .set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues)
                        .set(Keys.AFTER_GENERATE_HINT, defaultAfterGenerate))
                .supply(all(Container.class), new Generator<Container>() {
                    @Override
                    public Container generate(final Random r) {
                        return new Container(new Pojo());
                    }

                    @Override
                    public Hints hints() {
                        return Hints.afterGenerate(afterGenerate);
                    }
                })
                .toModel();
    }

    @Nested
    class OverwriteFieldsEnabledTest {

        private final boolean overWriteExistingValues = true;

        @Nested
        class WithoutSelectorTest {
            @Test
            void overwriteEnabled_doNotModify() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.DO_NOT_MODIFY));

                assertThat(result.pojo()).satisfies(SHOULD_BE_UNMODIFIED);
            }

            @Test
            void overwriteEnabled_applySelectors() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.APPLY_SELECTORS));

                assertThat(result.pojo()).satisfies(SHOULD_BE_UNMODIFIED);
            }

            @Test
            void overwriteEnabled_populateNulls() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS));

                assertThat(result.pojo().s1).is(RANDOM_STRING);
                assertThat(result.pojo().s2).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteEnabled_populateNullsAndDefaultPrimitives() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

                assertThat(result.pojo().s1).is(RANDOM_STRING);
                assertThat(result.pojo().s2).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteEnabled_populateAll() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL));

                assertThat(result.pojo()).satisfies(SHOULD_BE_RANDOM_POJO);
            }
        }

        @Nested
        class WithSelectorTest {
            @Test
            void overwriteEnabled_doNotModify() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.DO_NOT_MODIFY))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .lenient()
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_UNMODIFIED);
            }

            @Test
            void overwriteEnabled_applySelectors() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.APPLY_SELECTORS))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteEnabled_populateNulls() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteEnabled_populateNullsAndDefaultPrimitives() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteEnabled_populateAll() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }
        }
    }

    @Nested
    class OverwriteFieldsDisabledTest {

        private final boolean overWriteExistingValues = false;

        @Nested
        class WithoutSelectorTest {
            @Test
            void overwriteDisabled_doNotModify() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.DO_NOT_MODIFY));

                assertThat(result.pojo()).satisfies(SHOULD_BE_UNMODIFIED);
            }

            @Test
            void overwriteDisabled_applySelectors() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.APPLY_SELECTORS));

                assertThat(result.pojo()).satisfies(SHOULD_BE_UNMODIFIED);
            }

            @Test
            void overwriteDisabled_populateNulls() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS));

                assertThat(result.pojo().s1).is(RANDOM_STRING);
                assertThat(result.pojo().s2).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteDisabled_populateNullsAndDefaultPrimitives() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

                assertThat(result.pojo().s1).is(RANDOM_STRING);
                assertThat(result.pojo().s2).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteDisabled_populateAll() {
                final Container result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL));

                assertThat(result.pojo().s1).is(RANDOM_STRING);
                assertThat(result.pojo().s2).isEqualTo(INITIAL_STRING);
            }
        }

        /**
         * Selectors can overwrite values even when
         * {@link Keys#OVERWRITE_EXISTING_VALUES} is {@code false}.
         */
        @Nested
        class WithSelectorTest {
            @Test
            void overwriteDisabled_doNotModify() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.DO_NOT_MODIFY))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .lenient()
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_UNMODIFIED);
            }

            @Test
            void overwriteDisabled_applySelectors() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.APPLY_SELECTORS))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteDisabled_populateNulls() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteDisabled_populateNullsAndDefaultPrimitives() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteDisabled_populateAll() {
                final Container result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL))
                        .set(allStrings(), SELECTOR_STRING)
                        .set(allInts(), SELECTOR_INT)
                        .create();

                assertThat(result.pojo()).satisfies(SHOULD_BE_OVERWRITTEN_BY_SELECTOR);
            }
        }
    }
}
