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
import org.instancio.InstancioApi;
import org.instancio.Random;
import org.instancio.Size;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.object.WithStringArray;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorSizeTest {

    private static final int DEFAULT_MIN_SIZE = 0;
    private static final int DEFAULT_MAX_SIZE = 3;
    private static final String EXPECTED_STRING = "_value_";

    private static final StringsAbc EXPECTED_ABC = StringsAbc.builder()
            .a("_a_")
            .b("_b_")
            .c("_c_")
            .build();

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, DEFAULT_MIN_SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, DEFAULT_MAX_SIZE)
            .set(Keys.ARRAY_MIN_SIZE, DEFAULT_MIN_SIZE)
            .set(Keys.ARRAY_MAX_SIZE, DEFAULT_MAX_SIZE);

    @Test
    void atIndexGreaterThanCollectionMaxSizeSetting() {
        final int index = DEFAULT_MAX_SIZE + 10; // exceeds default collection size
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .set(elementOf(AbcListHolder::getAbcElements1).at(index), EXPECTED_ABC)
                .create();

        assertThat(result.getAbcElements1()).hasSize(index + 1);
        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(index));
    }

    @Test
    void atIndexGreaterThanArrayMaxSizeSetting() {
        final int index = DEFAULT_MAX_SIZE + 10; // exceeds default array size
        final WithStringArray result = Instancio.of(WithStringArray.class)
                .set(elementOf(WithStringArray::getValues).at(index), EXPECTED_STRING)
                .create();

        assertThat(result.getValues()).hasSize(index + 1);
        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[%s]".formatted(index));
    }

    @Test
    void elementOfWithEmptyArrayThrows() {
        final InstancioApi<WithStringArray> api = Instancio.of(WithStringArray.class)
                .generate(field(WithStringArray::getValues), gen -> gen.array().size(0))
                .set(elementOf(WithStringArray::getValues).first(), "x");

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("elementOf() selector at index 0 requires at least 1 elements")
                .hasMessageContaining("but an explicit size of 0 was set");
    }

    /**
     * Distinct from the field-selector cases below: here the explicit array size is set via
     * a {@code type} selector on an array of a leaf type ({@code String[]}).
     */
    @Test
    void atIndexExceedsArraySizeSetViaTypeSelectorThrows() {
        final InstancioApi<WithStringArray> api = Instancio.of(WithStringArray.class)
                .generate(all(String[].class), gen -> gen.array().size(1))
                .set(elementOf(WithStringArray::getValues).at(2), "x");

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("elementOf() selector at index 2 requires at least 3 elements")
                .hasMessageContaining("but an explicit size of 1 was set");
    }

    @Test
    void withElementsExceedingExplicitSize_andElementOfPresent_shouldReportWithOverflowError() {
        final StringsAbc abc1 = new StringsAbc();
        final StringsAbc abc2 = new StringsAbc();
        final StringsAbc abc3 = new StringsAbc();

        final InstancioApi<AbcArrayHolder> api = Instancio.of(AbcArrayHolder.class)
                .size(field(AbcArrayHolder::getAbcElements1), 2)
                .generate(field(AbcArrayHolder::getAbcElements1), gen -> gen.array().with(abc1, abc2, abc3))
                .set(elementOf(AbcArrayHolder::getAbcElements1).at(0), new StringsAbc());

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("more 'with()' elements")
                .hasMessageNotContaining("explicit max is -1");
    }

    @Nested
    class ElementOfIndexExceedsSpecifiedSize {

        private final int explicitSize = Instancio.gen().ints().range(0, 2).get();
        private final int requiredIndex = explicitSize + Instancio.gen().ints().range(0, 3).get();

        private void assertIndexExceedsSizeError(InstancioApi<?> api) {
            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() selector at index %s requires at least %s elements",
                            requiredIndex, requiredIndex + 1)
                    .hasMessageContaining("but an explicit size of %s was set.", explicitSize);
        }

        @Nested
        class ViaSizeApi {

            @Test
            void collection() {
                final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                        .size(field(AbcListHolder::getAbcElements1), explicitSize)
                        .set(elementOf(AbcListHolder::getAbcElements1).at(requiredIndex), new StringsAbc());

                assertIndexExceedsSizeError(api);
            }

            @Test
            void array() {
                final InstancioApi<AbcArrayHolder> api = Instancio.of(AbcArrayHolder.class)
                        .size(field(AbcArrayHolder::getAbcElements1), explicitSize)
                        .set(elementOf(AbcArrayHolder::getAbcElements1).at(requiredIndex), new StringsAbc());

                assertIndexExceedsSizeError(api);
            }
        }

        @Nested
        class ViaGeneratorSpec {

            @Test
            void collection() {
                final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                        .generate(field(AbcListHolder::getAbcElements1), gen -> gen.collection().size(explicitSize))
                        .set(elementOf(AbcListHolder::getAbcElements1).at(requiredIndex), new StringsAbc());

                assertIndexExceedsSizeError(api);
            }

            @Test
            void array() {
                final InstancioApi<AbcArrayHolder> api = Instancio.of(AbcArrayHolder.class)
                        .generate(field(AbcArrayHolder::getAbcElements1), gen -> gen.array().size(explicitSize))
                        .set(elementOf(AbcArrayHolder::getAbcElements1).at(requiredIndex), new StringsAbc());

                assertIndexExceedsSizeError(api);
            }

        }
    }

    /**
     * When the explicitly specified size is a range whose max can accommodate
     * the elementOf() index, the container should be widened within the range
     * rather than failing based on the size that happened to be drawn.
     */
    @Nested
    class ElementOfIndexSatisfiableBySizeRange {

        private static final int REQUIRED_INDEX = 9;
        private static final int MAX_SIZE = REQUIRED_INDEX + 1; // index satisfiable by the range max

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void collectionViaSizeApi() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), Size.range(0, MAX_SIZE))
                    .set(elementOf(AbcListHolder::getAbcElements1).at(REQUIRED_INDEX), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(MAX_SIZE);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(REQUIRED_INDEX));
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void arrayViaSizeApi() {
            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .size(field(AbcArrayHolder::getAbcElements1), Size.range(0, MAX_SIZE))
                    .set(elementOf(AbcArrayHolder::getAbcElements1).at(REQUIRED_INDEX), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(MAX_SIZE);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(REQUIRED_INDEX));
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void collectionViaGeneratorSpec() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .generate(field(AbcListHolder::getAbcElements1),
                            gen -> gen.collection().minSize(0).maxSize(MAX_SIZE))
                    .set(elementOf(AbcListHolder::getAbcElements1).at(REQUIRED_INDEX), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(MAX_SIZE);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(REQUIRED_INDEX));
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void arrayViaGeneratorSpec() {
            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .generate(field(AbcArrayHolder::getAbcElements1),
                            gen -> gen.array().minSize(0).maxSize(MAX_SIZE))
                    .set(elementOf(AbcArrayHolder::getAbcElements1).at(REQUIRED_INDEX), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(MAX_SIZE);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(REQUIRED_INDEX));
        }
    }

    /**
     * {@code minSize()} alone doesn't cap the container size, so the container
     * should be widened to fit the elementOf() index instead of reporting a size conflict.
     */
    @Nested
    class ElementOfIndexWithExplicitMinSizeOnly {

        private static final int REQUIRED_INDEX = 9;

        @Test
        void collection() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .generate(field(AbcListHolder::getAbcElements1), gen -> gen.collection().minSize(1))
                    .set(elementOf(AbcListHolder::getAbcElements1).at(REQUIRED_INDEX), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(REQUIRED_INDEX + 1);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(REQUIRED_INDEX));
        }

        @Test
        void array() {
            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .generate(field(AbcArrayHolder::getAbcElements1), gen -> gen.array().minSize(1))
                    .set(elementOf(AbcArrayHolder::getAbcElements1).at(REQUIRED_INDEX), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(REQUIRED_INDEX + 1);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(REQUIRED_INDEX));
        }
    }

    /**
     * The min-size implied by an elementOf() index selector should only apply to
     * containers matching the container selector's scopes. A same-typed container
     * outside the scopes must be neither widened nor reported as a size conflict.
     */
    @Nested
    class ScopedContainerSelector {

        private static final int REQUIRED_INDEX = 5;

        @Test
        void outOfScopeContainerShouldNotBeWidened() {
            final Item<String> expected = new Item<>("foo");

            final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                    .set(elementOf(all(List.class).within(scope(TwoListsOfItemString::getList1)))
                            .at(REQUIRED_INDEX), expected)
                    .create();

            assertThat(result.getList1()).hasSize(REQUIRED_INDEX + 1);
            assertThat(result.getList2()).hasSizeLessThanOrEqualTo(DEFAULT_MAX_SIZE);
            assertThatGraph(result).hasValuesEqualToExactlyIn(expected, "list1[%s]".formatted(REQUIRED_INDEX));
        }

        @Test
        void outOfScopeContainerWithExplicitSizeShouldNotTriggerSizeConflict() {
            final Item<String> expected = new Item<>("foo");

            final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                    .size(field(TwoListsOfItemString::getList2), 1)
                    .set(elementOf(all(List.class).within(scope(TwoListsOfItemString::getList1)))
                            .at(REQUIRED_INDEX), expected)
                    .create();

            assertThat(result.getList1()).hasSize(REQUIRED_INDEX + 1);
            assertThat(result.getList2()).hasSize(1);
            assertThatGraph(result).hasValuesEqualToExactlyIn(expected, "list1[%s]".formatted(REQUIRED_INDEX));
        }
    }

    @Nested
    class CustomHintContainerWidening {

        private static final Hints POPULATE_HINTS = Hints.afterGenerate(AfterGenerate.POPULATE_ALL);

        @Test
        void withArray() {
            final Generator<String[]> generator = new Generator<>() {
                @Override
                public String[] generate(final Random random) {
                    return new String[]{"a", "b"};
                }

                @Override
                public Hints hints() {
                    return POPULATE_HINTS;
                }
            };

            final int index = 4;

            final WithStringArray result = Instancio.of(WithStringArray.class)
                    .supply(field(WithStringArray::getValues), generator)
                    .set(elementOf(WithStringArray::getValues).at(index), EXPECTED_STRING)
                    .create();

            assertThat(result.getValues()).hasSize(index + 1);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "values[%s]".formatted(index));
        }

        @Test
        void withCollection() {
            final Generator<List<StringsAbc>> generator = new Generator<>() {
                @Override
                public List<StringsAbc> generate(final Random random) {
                    return new ArrayList<>();
                }

                @Override
                public Hints hints() {
                    return POPULATE_HINTS;
                }
            };

            final int index = 3;

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .supply(field(AbcListHolder::getAbcElements1), generator)
                    .set(elementOf(AbcListHolder::getAbcElements1).at(index), EXPECTED_ABC)
                    .create();

            assertThat(result.getAbcElements1()).hasSize(index + 1);
            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[%s]".formatted(index));
        }
    }

}
