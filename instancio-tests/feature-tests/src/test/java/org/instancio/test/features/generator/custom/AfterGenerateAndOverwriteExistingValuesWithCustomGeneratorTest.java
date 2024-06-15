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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.initialised.Container.OuterPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.fields;
import static org.instancio.test.support.conditions.Conditions.RANDOM_INTEGER;
import static org.instancio.test.support.conditions.Conditions.RANDOM_STRING;
import static org.instancio.test.support.pojo.initialised.Container.INITIAL_INT;
import static org.instancio.test.support.pojo.initialised.Container.INITIAL_STRING;
import static org.instancio.test.support.pojo.initialised.Container.OVERWRITTEN_BY_SELECTOR;
import static org.instancio.test.support.pojo.initialised.Container.RANDOM_POJO;
import static org.instancio.test.support.pojo.initialised.Container.REFERENCE_FIELD_REGEX;
import static org.instancio.test.support.pojo.initialised.Container.SELECTOR_INT;
import static org.instancio.test.support.pojo.initialised.Container.SELECTOR_STRING;
import static org.instancio.test.support.pojo.initialised.Container.UNMODIFIED_POJO;

/**
 * Tests all possible combinations of:
 *
 * <ul>
 *   <li>{@link Keys#OVERWRITE_EXISTING_VALUES} and</li>
 *   <li>{@link AfterGenerate} hint from a custom generator</li>
 * </ul>
 *
 * <p>The generator returns an instance of the {@link OuterPojo} class,
 * which is half-populated and contains nested objects.
 *
 * <p>There's probably some overlap between this and some other
 * tests related to {@link AfterGenerate}.
 *
 * @see AfterGenerateAndOverwriteExistingValuesTest
 */
@FeatureTag({Feature.AFTER_GENERATE, Feature.GENERATOR, Feature.OVERWRITE_EXISTING_VALUES})
@ExtendWith(InstancioExtension.class)
class AfterGenerateAndOverwriteExistingValuesWithCustomGeneratorTest {

    private Model<OuterPojo> model(final boolean overwriteExistingValues, final AfterGenerate afterGenerate) {
        // Default AfterGenerate setting shouldn't matter since the Generator hint should take precedence.
        // Using a random value to ensure this is correct.
        final AfterGenerate defaultAfterGenerate = Instancio.gen().enumOf(AfterGenerate.class).get();

        return Instancio.of(OuterPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues)
                        .set(Keys.AFTER_GENERATE_HINT, defaultAfterGenerate))
                .ignore(fields().matching(REFERENCE_FIELD_REGEX))
                .supply(all(OuterPojo.class), new Generator<OuterPojo>() {
                    @Override
                    public OuterPojo generate(final Random r) {
                        return new OuterPojo();
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
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.DO_NOT_MODIFY));

                assertThat(result).is(UNMODIFIED_POJO);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(UNMODIFIED_POJO);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(UNMODIFIED_POJO);
            }

            @Test
            void overwriteEnabled_applySelectors() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.APPLY_SELECTORS));

                assertThat(result).is(UNMODIFIED_POJO);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(UNMODIFIED_POJO);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(UNMODIFIED_POJO);
            }

            @Test
            void overwriteEnabled_populateNulls() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.POPULATE_NULLS));

                assertThat(result.getN1()).isZero();
                assertThat(result.getS1()).is(RANDOM_STRING);
                assertThat(result.getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getS2()).isEqualTo(INITIAL_STRING);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .allSatisfy(p -> {
                            assertThat(p.getN1()).isZero();
                            assertThat(p.getS1()).is(RANDOM_STRING);
                            assertThat(p.getN2()).isEqualTo(INITIAL_INT);
                            assertThat(p.getS2()).isEqualTo(INITIAL_STRING);
                        });

                assertThat(result.getP1()).satisfies(RANDOM_POJO);

                assertThat(result.getP2()).isSameAs(result.get_initialP2());
                assertThat(result.getP2().getN1()).isZero();
                assertThat(result.getP2().getS1()).is(RANDOM_STRING);
                assertThat(result.getP2().getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getP2().getS2()).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteEnabled_populateNullsAndDefaultPrimitives() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

                assertThat(result.getN1()).is(RANDOM_INTEGER);
                assertThat(result.getS1()).is(RANDOM_STRING);
                assertThat(result.getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getS2()).isEqualTo(INITIAL_STRING);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .allSatisfy(p -> {
                            assertThat(p.getN1()).is(RANDOM_INTEGER);
                            assertThat(p.getS1()).is(RANDOM_STRING);
                            assertThat(p.getN2()).isEqualTo(INITIAL_INT);
                            assertThat(p.getS2()).isEqualTo(INITIAL_STRING);
                        });

                assertThat(result.getP1()).satisfies(RANDOM_POJO);

                assertThat(result.getP2()).isSameAs(result.get_initialP2());
                assertThat(result.getP2().getN1()).is(RANDOM_INTEGER);
                assertThat(result.getP2().getS1()).is(RANDOM_STRING);
                assertThat(result.getP2().getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getP2().getS2()).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteEnabled_populateAll() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL));

                assertThat(result).is(RANDOM_POJO);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
                assertThat(result.getList2())
                        .as("new instance")
                        .isNotSameAs(result.get_initialList2())
                        .hasSizeGreaterThan(1)
                        .are(RANDOM_POJO);

                assertThat(result.getP1()).satisfies(RANDOM_POJO);

                assertThat(result.getP2())
                        .as("new instance")
                        .isNotSameAs(result.get_initialP2())
                        .satisfies(RANDOM_POJO);
            }
        }

        @Nested
        class WithSelectorTest {
            @Test
            void overwriteEnabled_doNotModify() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.DO_NOT_MODIFY))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .lenient()
                        .create();

                assertThat(result).is(UNMODIFIED_POJO);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(UNMODIFIED_POJO);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(UNMODIFIED_POJO);
            }

            @Test
            void overwriteEnabled_applySelectors() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.APPLY_SELECTORS))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteEnabled_populateNulls() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1())
                        .as("new instance")
                        .satisfies(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteEnabled_populateNullsAndDefaultPrimitives() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues,
                                AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteEnabled_populateAll() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList2())
                        .as("new instance")
                        .hasSizeGreaterThan(1)
                        .isNotSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP2())
                        .as("new instance")
                        .isNotSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
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
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.DO_NOT_MODIFY));

                assertThat(result).is(UNMODIFIED_POJO);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(UNMODIFIED_POJO);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(UNMODIFIED_POJO);
            }

            @Test
            void overwriteDisabled_applySelectors() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.APPLY_SELECTORS));

                assertThat(result).is(UNMODIFIED_POJO);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(UNMODIFIED_POJO);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(UNMODIFIED_POJO);
            }

            @Test
            void overwriteDisabled_populateNulls() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.POPULATE_NULLS));

                assertThat(result.getN1()).isZero();
                assertThat(result.getS1()).is(RANDOM_STRING);
                assertThat(result.getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getS2()).isEqualTo(INITIAL_STRING);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .allSatisfy(p -> {
                            assertThat(p.getN1()).isZero();
                            assertThat(p.getS1()).is(RANDOM_STRING);
                            assertThat(p.getN2()).isEqualTo(INITIAL_INT);
                            assertThat(p.getS2()).isEqualTo(INITIAL_STRING);
                        });

                assertThat(result.getP1()).as("new instance").isNotNull();
                assertThat(result.getP1()).is(RANDOM_POJO);

                assertThat(result.getP2()).isSameAs(result.get_initialP2());
                assertThat(result.getP2().getN1()).isZero();
                assertThat(result.getP2().getS1()).is(RANDOM_STRING);
                assertThat(result.getP2().getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getP2().getS2()).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteDisabled_populateNullsAndDefaultPrimitives() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));

                assertThat(result.getN1()).is(RANDOM_INTEGER);
                assertThat(result.getS1()).is(RANDOM_STRING);
                assertThat(result.getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getS2()).isEqualTo(INITIAL_STRING);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .allSatisfy(p -> {
                            assertThat(p.getN1()).is(RANDOM_INTEGER);
                            assertThat(p.getS1()).is(RANDOM_STRING);
                            assertThat(p.getN2()).isEqualTo(INITIAL_INT);
                            assertThat(p.getS2()).isEqualTo(INITIAL_STRING);
                        });

                assertThat(result.getP1()).satisfies(RANDOM_POJO);

                assertThat(result.getP2()).isSameAs(result.get_initialP2());
                assertThat(result.getP2().getN1()).is(RANDOM_INTEGER);
                assertThat(result.getP2().getS1()).is(RANDOM_STRING);
                assertThat(result.getP2().getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getP2().getS2()).isEqualTo(INITIAL_STRING);
            }

            @Test
            void overwriteDisabled_populateAll() {
                final OuterPojo result = Instancio.create(model(overWriteExistingValues,
                        AfterGenerate.POPULATE_ALL));

                assertThat(result.getN1()).is(RANDOM_INTEGER);
                assertThat(result.getS1()).is(RANDOM_STRING);
                assertThat(result.getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getS2()).isEqualTo(INITIAL_STRING);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .allSatisfy(p -> {
                            assertThat(p.getN1()).is(RANDOM_INTEGER);
                            assertThat(p.getS1()).is(RANDOM_STRING);
                            assertThat(p.getN2()).isEqualTo(INITIAL_INT);
                            assertThat(p.getS2()).isEqualTo(INITIAL_STRING);
                        });

                assertThat(result.getP1()).satisfies(RANDOM_POJO);

                assertThat(result.getP2()).isSameAs(result.get_initialP2());
                assertThat(result.getP2().getN1()).is(RANDOM_INTEGER);
                assertThat(result.getP2().getS1()).is(RANDOM_STRING);
                assertThat(result.getP2().getN2()).isEqualTo(INITIAL_INT);
                assertThat(result.getP2().getS2()).isEqualTo(INITIAL_STRING);
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
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.DO_NOT_MODIFY))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .lenient()
                        .create();

                assertThat(result).is(UNMODIFIED_POJO);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(UNMODIFIED_POJO);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(UNMODIFIED_POJO);
            }

            @Test
            void overwriteDisabled_applySelectors() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues,
                                AfterGenerate.APPLY_SELECTORS))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).isNull();
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).isNull();

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteDisabled_populateNulls() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_NULLS))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteDisabled_populateNullsAndDefaultPrimitives() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues,
                                AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }

            @Test
            void overwriteDisabled_populateAll() {
                final OuterPojo result = Instancio.of(model(overWriteExistingValues, AfterGenerate.POPULATE_ALL))
                        .set(allInts(), SELECTOR_INT)
                        .set(allStrings(), SELECTOR_STRING)
                        .create();

                assertThat(result).is(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
                assertThat(result.getList2()).hasSize(1)
                        .isSameAs(result.get_initialList2())
                        .are(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

                assertThat(result.getP2())
                        .isSameAs(result.get_initialP2())
                        .satisfies(OVERWRITTEN_BY_SELECTOR);
            }
        }
    }
}
