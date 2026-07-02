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

import org.instancio.Assignment;
import org.instancio.ElementOfSelector;
import org.instancio.IndexedElementSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.InstancioClassApi;
import org.instancio.ScopeableSelector;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@SuppressWarnings("NullAway")
@ParameterizedClass
@MethodSource("containers")
@FeatureTag({Feature.ASSIGN, Feature.ELEMENT_OF_SELECTOR})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorAssignTest {

    private static final int SIZE = 5;
    private static final int LAST_INDEX = SIZE - 1;
    private static final String EXPECTED_STRING = "_value_";
    private static final StringsAbc EXPECTED_ABC = StringsAbc.builder()
            .a("_a_")
            .b("_b_")
            .c("_c_")
            .build();

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Parameter(0)
    private Class<?> rootClass;

    private Class<?> getNestedClass() {
        return rootClass == AbcListHolder.class
                ? AbcListHolder.Nested.class
                : AbcArrayHolder.Nested.class;
    }

    static Stream<Arguments> containers() {
        return Stream.of(
                Arguments.of(AbcListHolder.class),
                Arguments.of(AbcArrayHolder.class));
    }

    /**
     * Field-to-field assignment within the same element (same index).
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SameElement {

        @ValueSource(ints = {0, 1, 2, 3, 4, 10})
        @ParameterizedTest
        void elementField(final int index) {
            final TargetSelector origin = elementOf(field(rootClass, "abcElements2"))
                    .at(index)
                    .field(StringsAbc::getB);

            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                    .at(index)
                    .field(StringsAbc::getA);

            final Object result = Instancio.of(rootClass)
                    .withUnique(allStrings())
                    .assign(valueOf(origin).to(destination))
                    .set(origin, EXPECTED_STRING)
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                            "abcElements2[%s].a".formatted(index),
                            "abcElements2[%s].b".formatted(index),
                    });
        }

        @ValueSource(ints = {0, 1, 2, 3, 4, 10})
        @ParameterizedTest
        void elementFieldWithMapper(final int index) {
            final TargetSelector origin = elementOf(field(rootClass, "abcElements2"))
                    .at(index)
                    .field(StringsAbc::getA);

            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                    .at(index)
                    .field(StringsAbc::getB);

            final String prefix = "prefix";
            final Object result = Instancio.of(rootClass)
                    .withUnique(allStrings())
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination).as(originValue -> prefix + EXPECTED_STRING))
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements2[%s].a".formatted(index))
                    .hasValuesEqualToExactlyIn(prefix + EXPECTED_STRING, "abcElements2[%s].b".formatted(index));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class WithinSameContainer {

        @RepeatedTest(Constants.SAMPLE_SIZE_DDD)
        void elementOfFieldConditional() {
            final int originIndex = Instancio.gen().ints().range(0, 20).get();
            final int destinationIndex = Instancio.gen().ints().range(0, 20).get();

            final TargetSelector origin = elementOf(field(rootClass, "abcElements2"))
                    .at(originIndex)
                    .field(StringsAbc::getA);

            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                    .at(destinationIndex)
                    .field(StringsAbc::getB);

            final Object result = Instancio.of(rootClass)
                    .withUnique(allStrings())
                    .assign(valueOf(origin)
                            .to(destination)
                            .when((String originValue) -> originValue.contains("A")))
                    .create();

            // Use AbcListHolder-like access pattern; both holders share the same field names
            final List<StringsAbc> elements2 = getAbcElements2(result);
            final String a = elements2.get(originIndex).getA();
            final String b = elements2.get(destinationIndex).getB();

            if (a.contains("A")) {
                assertThat(a).isEqualTo(b);
            } else {
                assertThat(a).isNotEqualTo(b);
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class FirstToLast {

            @Test
            void wholeElement() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).first();
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1")).last();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0,%s]".formatted(LAST_INDEX));
            }

            @Test
            void elementField() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1"))
                        .first()
                        .field(StringsAbc::getB);

                final TargetSelector destination = elementOf(field(rootClass, "abcElements1"))
                        .last()
                        .field(StringsGhi::getH);

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_STRING)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                        "abcElements1[0].b",
                        "abcElements1[%s].def.ghi.h".formatted(LAST_INDEX),
                });
            }

            @Test
            void wholeElement_matchingPredicate() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).first();
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1")).last();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination)
                                .when((StringsAbc o) -> "_a_".equals(o.getA())))
                        .create();

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0,%s]".formatted(LAST_INDEX));
            }

            @Test
            void wholeElement_nonMatchingPredicate() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).first();
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1")).last();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination).when((StringsAbc o) -> false)) // never matches
                        .create();

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0]");
            }

            @Test
            void wholeElement_withMapper() {
                final IndexedElementSelector origin = elementOf(field(rootClass, "abcElements1")).first();
                final IndexedElementSelector destination = elementOf(field(rootClass, "abcElements1")).last();
                final StringsAbc mapped = StringsAbc.builder().a("X").b("Y").c("Z").build();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination).as((StringsAbc o) -> mapped))
                        .create();

                assertThat(getAbcElements1(result).get(LAST_INDEX)).isEqualTo(mapped);
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Multiple {

            @Test
            void wholeElement() {
                final TargetSelector originThree = elementOf(field(rootClass, "abcElements1")).at(3);  // 3 -> 0
                final TargetSelector destinationZero = elementOf(field(rootClass, "abcElements1")).at(0); // 0 -> 2
                final TargetSelector destinationTwo = elementOf(field(rootClass, "abcElements1")).at(2); // 2 -> 4
                final TargetSelector destinationFour = elementOf(field(rootClass, "abcElements1")).at(4);

                final Assignment[] assignments = {
                        valueOf(originThree).to(destinationZero),
                        valueOf(destinationZero).to(destinationTwo),
                        valueOf(destinationTwo).to(destinationFour)
                };

                final Object result = Instancio.of(rootClass)
                        .set(originThree, EXPECTED_ABC)
                        .assign(assignments)
                        .create();

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0,2,3,4]");
            }

            @Test
            void elementField() {
                final ElementOfSelector elementOfSelector = elementOf(field(rootClass, "abcElements1"));
                final TargetSelector originThree = elementOfSelector.at(3).field(StringsDef::getE);  // 3 -> 0
                final TargetSelector destinationZero = elementOfSelector.at(0).field(StringsDef::getE); // 0 -> 2
                final TargetSelector destinationTwo = elementOfSelector.at(2).field(StringsDef::getE); // 2 -> 4
                final TargetSelector destinationFour = elementOfSelector.at(4).field(StringsDef::getE);

                final Assignment[] assignments = {
                        valueOf(originThree).to(destinationZero),
                        valueOf(destinationZero).to(destinationTwo),
                        valueOf(destinationTwo).to(destinationFour)
                };

                final Object result = Instancio.of(rootClass)
                        .set(originThree, EXPECTED_STRING)
                        .assign(assignments)
                        .create();

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements1[0,2,3,4].def.e");
            }
        }

        /**
         * Pick an origin index and assign to all other indices
         * except itself and one other randomly picked index.
         *
         * <pre>
         * - start with all indices: [0,1,2,3,4]
         * - pick 'exceptIndex' and remove it, e.g. 3 => [0,1,2,4]
         * - pick 'originIndex' and remove it, e.g. 1 => [0,2,4]
         * - set origin index value via {@code set()}
         * - copy origin via {@code assign()} using {@code except(origin, except)}
         * </pre>
         */
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class OriginToOtherIndicesUsingExcept {

            private Set<Integer> remainingIndices;
            private int originIndex;
            private IndexedElementSelector origin;
            private IndexedElementSelector destination;

            @BeforeEach
            void setUp() {
                remainingIndices = new HashSet<>();
                for (int i = 0; i < 5; i++) {
                    remainingIndices.add(i);
                }

                final int exceptIndex = Instancio.gen().oneOf(remainingIndices).get();
                remainingIndices.remove(exceptIndex);

                originIndex = Instancio.gen().oneOf(remainingIndices).get();
                remainingIndices.remove(originIndex);

                origin = elementOf(field(rootClass, "abcElements1")).at(originIndex);
                destination = elementOf(field(rootClass, "abcElements1")).except(originIndex, exceptIndex);
            }

            @RepeatedTest(Constants.SAMPLE_SIZE_DD)
            void wholeElement() {
                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                final String[] subtrees = {
                        "abcElements1[%s]".formatted(originIndex),
                        "abcElements1" + remainingIndices,
                };

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtrees);
            }

            @RepeatedTest(Constants.SAMPLE_SIZE_DD)
            void elementField() {
                final TargetSelector originField = origin.field(StringsDef::getF);
                final TargetSelector destinationField = destination.field(StringsAbc::getC);

                final Object result = Instancio.of(rootClass)
                        .set(originField, EXPECTED_STRING)
                        .assign(valueOf(originField).to(destinationField))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                        "abcElements1[" + originIndex + "].def.f",
                        "abcElements1" + remainingIndices + ".c"
                });
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class OutOfRangeIndices {

            /**
             * Excluded indices beyond the collection size should be
             * silently ignored by {@link ElementOfSelector#except(int...)}.
             */
            @Test
            void exceptWithIndexBeyondSizeIsIgnored() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).at(0);
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1"))
                        .except(0, 100);

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0,1,2,3,4]");
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class AllIndices {

            @Test
            void originFields() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).field(StringsAbc::getA);
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1")).field(StringsAbc::getB);

                final Object result = Instancio.of(rootClass)
                        .withUnique(allStrings())
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThat(getAbcElements1(result))
                        .allSatisfy(e -> assertThat(e.getA()).isEqualTo(e.getB()));
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class RangeDestination {

            @RepeatedTest(Constants.SAMPLE_SIZE_DD)
            void destination() {
                final int destStartIdx = Instancio.gen().ints().range(1, 3).get();
                final int destEndIdx = Instancio.gen().ints().range(destStartIdx, destStartIdx + SIZE).get();
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).at(0);
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1"))
                        .range(destStartIdx, destEndIdx);

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                final String indices = IntStream.rangeClosed(destStartIdx, destEndIdx)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(","));

                assertThatGraph(result)
                        .hasValuesEqualToExactlyIn(EXPECTED_ABC, "abcElements1[0,%s]".formatted(indices));
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AcrossSiblingContainers {

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Collection1First_To_Collection2Last {

            @Test
            void wholeElement() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).first();
                final TargetSelector destination = elementOf(field(rootClass, "abcElements2")).last();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, new String[]{
                        "abcElements1[0]",
                        "abcElements2[%s]".formatted(LAST_INDEX),
                });
            }

            @Test
            void elementField() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1"))
                        .first()
                        .field(StringsGhi::getG);

                final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                        .last()
                        .field(StringsAbc::getC);

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_STRING)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                        "abcElements1[0].def.ghi.g",
                        "abcElements2[%s].c".formatted(LAST_INDEX),
                });
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Collection1Last_To_Collection2First {

            @Test
            void wholeElement() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).last();
                final TargetSelector destination = elementOf(field(rootClass, "abcElements2")).first();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, new String[]{
                        "abcElements1[%s]".formatted(LAST_INDEX),
                        "abcElements2[0]",
                });
            }

            @Test
            void elementField() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements1"))
                        .last()
                        .field(StringsDef::getD);

                final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                        .first()
                        .field(StringsAbc::getC);

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_STRING)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                        "abcElements1[%s].def.d".formatted(LAST_INDEX),
                        "abcElements2[0].c",
                });
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Collection2Last_To_Collection1First {

            @Test
            void wholeElement() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements2")).last();
                final TargetSelector destination = elementOf(field(rootClass, "abcElements1")).first();

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_ABC)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, new String[]{
                        "abcElements2[%s]".formatted(LAST_INDEX),
                        "abcElements1[0]",
                });
            }

            @Test
            void elementField() {
                final TargetSelector origin = elementOf(field(rootClass, "abcElements2"))
                        .last()
                        .field(StringsDef::getD);

                final TargetSelector destination = elementOf(field(rootClass, "abcElements1"))
                        .first()
                        .field(StringsGhi::getH);

                final Object result = Instancio.of(rootClass)
                        .set(origin, EXPECTED_STRING)
                        .assign(valueOf(origin).to(destination))
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                        "abcElements2[%s].def.d".formatted(LAST_INDEX),
                        "abcElements1[0].def.ghi.h",
                });
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Multiple {

            @Test
            void wholeElement() {
                final TargetSelector originThree = elementOf(field(rootClass, "abcElements1")).at(3); // abcElements1[3] to abcElements2[0]
                final TargetSelector destinationZero = elementOf(field(rootClass, "abcElements2")).at(0); // abcElements2[0] to abcElements1[2]
                final TargetSelector destinationTwo = elementOf(field(rootClass, "abcElements1")).at(2); // abcElements1[2] to abcElements2[1]
                final TargetSelector destinationOne = elementOf(field(rootClass, "abcElements2")).at(1);

                final Assignment[] assignments = {
                        valueOf(originThree).to(destinationZero),
                        valueOf(destinationZero).to(destinationTwo),
                        valueOf(destinationTwo).to(destinationOne)
                };

                final Object result = Instancio.of(rootClass)
                        .set(originThree, EXPECTED_ABC)
                        .assign(assignments)
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, new String[]{
                        "abcElements1[2,3]",
                        "abcElements2[0,1]"
                });
            }

            @Test
            void elementField() {
                final TargetSelector originThree = elementOf(field(rootClass, "abcElements1"))
                        .at(3).field(StringsGhi::getG); // abcElements1[3] to abcElements2[0]

                final TargetSelector destinationZero = elementOf(field(rootClass, "abcElements2"))
                        .at(0).field(StringsGhi::getG); // abcElements2[0] to abcElements1[2]

                final TargetSelector destinationTwo = elementOf(field(rootClass, "abcElements1"))
                        .at(2).field(StringsGhi::getG); // abcElements1[2] to abcElements2[1]

                final TargetSelector destinationOne = elementOf(field(rootClass, "abcElements2"))
                        .at(1).field(StringsGhi::getG);

                final Assignment[] assignments = {
                        valueOf(originThree).to(destinationZero),
                        valueOf(destinationZero).to(destinationTwo),
                        valueOf(destinationTwo).to(destinationOne)
                };

                final Object result = Instancio.of(rootClass)
                        .set(originThree, EXPECTED_STRING)
                        .assign(assignments)
                        .create();

                assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                        "abcElements1[2,3].def.ghi.g",
                        "abcElements2[0,1].def.ghi.g"
                });
            }
        }
    }

    /**
     * Copy multiple across containers at different depths:
     *
     * <pre>
     * - abcElements1[3] to nested.abcElements2[0]
     * - nested.abcElements2[0] to abcElements1[2]
     * - abcElements1[2] to nested.abcElements2[4]
     * </pre>
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AcrossDifferentDepths {

        @Test
        void wholeElement() {
            final TargetSelector originThree = elementOf(field(rootClass, "abcElements1")).at(3);
            final TargetSelector destinationZero = elementOf(field(getNestedClass(), "abcElements2")).at(0);
            final TargetSelector destinationTwo = elementOf(field(rootClass, "abcElements1")).at(2);
            final TargetSelector destinationFour = elementOf(field(getNestedClass(), "abcElements2")).at(4);

            final Assignment[] assignments = {
                    valueOf(originThree).to(destinationZero),
                    valueOf(destinationZero).to(destinationTwo),
                    valueOf(destinationTwo).to(destinationFour)
            };

            final Object result = Instancio.of(rootClass)
                    .set(originThree, EXPECTED_ABC)
                    .assign(assignments)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, new String[]{
                    "abcElements1[2,3]",
                    "nested.abcElements2[0,4]"
            });
        }

        @Test
        void elementField() {
            final TargetSelector originThree = elementOf(field(rootClass, "abcElements1")).at(3).field(StringsDef::getF);
            final TargetSelector destinationZero = elementOf(field(getNestedClass(), "abcElements2")).at(0).field(StringsDef::getF);
            final TargetSelector destinationTwo = elementOf(field(rootClass, "abcElements1")).at(2).field(StringsDef::getF);
            final TargetSelector destinationFour = elementOf(field(getNestedClass(), "abcElements2")).at(4).field(StringsDef::getF);

            final Assignment[] assignments = {
                    valueOf(originThree).to(destinationZero),
                    valueOf(destinationZero).to(destinationTwo),
                    valueOf(destinationTwo).to(destinationFour)
            };

            final Object result = Instancio.of(rootClass)
                    .set(originThree, EXPECTED_STRING)
                    .assign(assignments)
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "abcElements1[2,3].def.f",
                    "nested.abcElements2[0,4].def.f"
            });
        }
    }

    /**
     * Assignments that mix an {@code elementOf()} endpoint with a regular,
     * non-{@code elementOf()} selector.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NonElementEndpoints {

        @ValueSource(ints = {0, 1, 2, 3, 4, 10})
        @ParameterizedTest
        void nonElementOrigin_toElement(final int index) {
            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                    .at(index)
                    .field(StringsDef::getD);

            // 0       1      2      3
            // root -> abc -> def -> e
            final ScopeableSelector origin = field(StringsDef::getE).atDepth(3);

            final Object result = Instancio.of(rootClass)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "abc.def.e",
                    "abcElements2[%s].def.d".formatted(index)
            });
        }

        @Test
        void multipleRegularOrigins_toElements() {
            final TargetSelector dest0 = elementOf(field(rootClass, "abcElements2"))
                    .at(0)
                    .field(StringsDef::getD);

            final TargetSelector dest1 = elementOf(field(rootClass, "abcElements2"))
                    .at(1)
                    .field(StringsDef::getD);

            final ScopeableSelector originE = field(StringsDef::getE).atDepth(3);
            final ScopeableSelector originF = field(StringsDef::getF).atDepth(3);

            final String expectedE = "_e_";
            final String expectedF = "_f_";

            final var result = Instancio.of(rootClass)
                    .set(originE, expectedE)
                    .set(originF, expectedF)
                    .assign(valueOf(originE).to(dest0), valueOf(originF).to(dest1))
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(expectedE, new String[]{"abc.def.e", "abcElements2[0].def.d"})
                    .hasValuesEqualToExactlyIn(expectedF, new String[]{"abc.def.f", "abcElements2[1].def.d"});
        }
    }

    /**
     * Assignments that supply the value via {@code valueOf(...).generate(...)}
     * or {@code valueOf(...).set(...)} rather than copying from another element.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValueOfGenerateAndSet {

        @ValueSource(ints = {0, 1, 2, 3, 4, 10})
        @ParameterizedTest
        void elementField_valueOf_generate(final int index) {
            final TargetSelector target = elementOf(field(rootClass, "abcElements2"))
                    .at(index)
                    .field(StringsAbc::getA);

            final Object result = Instancio.of(rootClass)
                    .assign(valueOf(target).generate(gen -> gen.oneOf(EXPECTED_STRING)))
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "abcElements2[%s].a".formatted(index));
        }

        @Test
        void multipleValueOfElementOfSetCallsCoexist() {
            final TargetSelector firstA = elementOf(field(rootClass, "abcElements1"))
                    .first()
                    .field(StringsAbc::getA);

            final TargetSelector lastB = elementOf(field(rootClass, "abcElements1"))
                    .last()
                    .field(StringsAbc::getB);

            final Object result = Instancio.of(rootClass)
                    .assign(
                            valueOf(firstA).set("first-a"),
                            valueOf(lastB).set("last-b"))
                    .create();

            final List<StringsAbc> elements = getAbcElements1(result);
            assertThat(elements.get(0).getA()).isEqualTo("first-a");
            assertThat(elements.get(LAST_INDEX).getB()).isEqualTo("last-b");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmptyContainers {

        @Test
        void originAndDestination_viaSettings() {
            final TargetSelector origin = elementOf(field(rootClass, "abcElements1"));
            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"));

            final Object result = Instancio.of(rootClass)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 0)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 0)
                    .withSetting(Keys.ARRAY_MIN_SIZE, 0)
                    .withSetting(Keys.ARRAY_MAX_SIZE, 0)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThat(getAbcElements1(result)).isEmpty();
            assertThat(getAbcElements2(result)).isEmpty();
        }

        @Test
        void origin_viaSelector() {
            final TargetSelector origin = elementOf(field(rootClass, "abcElements1"));
            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"));

            final InstancioApi<?> api = Instancio.of(rootClass)
                    .size(field(rootClass, "abcElements1"), 0)
                    .assign(valueOf(origin).to(destination));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                    .hasMessageContaining("The following assignments could not be applied")
                    .hasMessageContaining("from [elementOf(field(%s, \"abcElements1\"))]",
                            rootClass.getSimpleName());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Validation {

        @Test
        void subFieldSelectorMatchingNoElementChildThrows() {
            // Field "abc" is declared on AbcListHolder (the root),
            // not on the element type StringsAbc
            final TargetSelector origin = elementOf(field(rootClass, "abcElements1"))
                    .first()
                    .target(field(rootClass, "abc"));

            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                    .first()
                    .target(field(rootClass, "abc"));

            final Assignment assignment = valueOf(origin).to(destination);
            final InstancioClassApi<?> api = Instancio.of(rootClass);

            assertThatThrownBy(() -> api.assign(assignment))
                    .isInstanceOf(InstancioException.class)
                    .hasMessageContaining("did not match any field within the element subtree");
        }

        @Test
        void subFieldDestinationMatchingNoElementChildThrows() {
            final TargetSelector origin = elementOf(field(rootClass, "abcElements1"))
                    .first()
                    .field(StringsAbc::getA);

            final TargetSelector destination = elementOf(field(rootClass, "abcElements2"))
                    .first()
                    .target(field(rootClass, "abc")); // field exists in rootClass but not in abcElements2

            final Assignment assignment = valueOf(origin).to(destination);
            final InstancioClassApi<?> api = Instancio.of(rootClass);

            assertThatThrownBy(() -> api.assign(assignment))
                    .isInstanceOf(InstancioException.class)
                    .hasMessageContaining("did not match any field within the element subtree")
                    .hasMessageContaining(destination.toString());
        }

        @Test
        void elementOfOrigin_toNonElementOfDestination_throwsException() {
            final TargetSelector origin = elementOf(field(rootClass, "abcElements1"))
                    .first()
                    .field(StringsAbc::getA);

            final TargetSelector destination = field(rootClass, "abc");

            final Assignment assignment = valueOf(origin).to(destination);
            final InstancioClassApi<?> api = Instancio.of(rootClass);

            assertThatThrownBy(() -> api.assign(assignment))
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() origin requires an elementOf() destination");
        }

        /**
         * The reverse of {@link #elementOfOrigin_toNonElementOfDestination_throwsException()}
         * is supported: a regular (non-elementOf) origin can be assigned to an elementOf()
         * destination, broadcasting a single value into each matched element.
         */
        @Test
        void nonElementOfOrigin_toElementOfDestination_isSupported() {
            // origin: the root 'abc' element's 'a' field (a single, non-elementOf value)
            final TargetSelector origin = field(StringsAbc::getA)
                    .within(field(rootClass, "abc").toScope());

            // destination: the 'a' field of every element in abcElements1
            final TargetSelector destination = elementOf(field(rootClass, "abcElements1"))
                    .field(StringsAbc::getA);

            final Object result = Instancio.of(rootClass)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThat(getAbcElements1(result))
                    .isNotEmpty()
                    .allSatisfy(element -> assertThat(element.getA()).isEqualTo(EXPECTED_STRING));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Unsupported {

        /**
         * Assign across containers, from {@code abcElements1} to {@code abcElements1}.
         */
        @Test
        @FeatureTag(Feature.UNSUPPORTED)
        void originElements_allIndices_toDestinationInAnotherContainer() {
            final Selector origin = field(rootClass, "abcElements1");
            final TargetSelector destination = field(rootClass, "abcElements2");

            final List<?> abcList = IntStream.range(0, SIZE)
                    .mapToObj(i -> StringsAbc.builder().a("_value%s_".formatted(i)).build())
                    .toList();

            final Object result = Instancio.of(rootClass)
                    .size(origin, SIZE)
                    .generate(all(StringsAbc.class).within(origin.toScope()), gen -> gen.emit().items(abcList))
                    .assign(valueOf(elementOf(origin)) // since no index specified, selects all indices
                            .to(elementOf(destination)))
                    .create();

            final Object last = abcList.get(LAST_INDEX);

            assertThatGraph(result).hasValuesEqualToExactlyIn(last, new String[]{
                    "abcElements1[%s]".formatted(LAST_INDEX),
                    "abcElements2[*]" // last abcElements1 value gets assigned to all abcElements2
            });
        }

        /**
         * Assign within container, from {@code abcElements1[1,2]} to {@code abcElements1[3,4]}
         */
        @Test
        @FeatureTag(Feature.UNSUPPORTED)
        void origin_range_toDestination_withinSameContainer() {
            final StringsAbc origin1 = StringsAbc.builder().a("_origin1_").build();
            final StringsAbc origin2 = StringsAbc.builder().a("_origin2_").build();

            final TargetSelector origin = elementOf(field(rootClass, "abcElements1")).range(1, 2);
            final TargetSelector destination = elementOf(field(rootClass, "abcElements1")).range(3, 4);

            final Object result = Instancio.of(rootClass)
                    .set(elementOf(field(rootClass, "abcElements1")).at(1), origin1)
                    .set(elementOf(field(rootClass, "abcElements1")).at(2), origin2)
                    .assign(valueOf(origin).to(destination))
                    .create();

            // Assigning origins that match multiple targets is not supported,
            // and the behaviour is undefined. In this case, origin selector
            // is a `range(1,2)` which matches 2 indices, so the assignment
            // happens to pick the value of the highest index.
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(origin1, "abcElements1[1]")
                    .hasValuesEqualToExactlyIn(origin2, "abcElements1[2,3,4]");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NullSafety {

        private final TargetSelector container = field(rootClass, "abcElements1");

        @Test
        void nullOriginElement_skipsAssignment() {
            final TargetSelector origin = elementOf(container).first().field(StringsAbc::getA);
            final TargetSelector destination = elementOf(container).last().field(StringsAbc::getA);

            final Object preBuilt = listOrArrayWithNullAt(0);

            final Object result = Instancio.of(rootClass)
                    .set(container, preBuilt)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(StringsAbc.class, null, "abcElements1[0]")
                    .hasValuesEqualToExactlyIn("a", "abcElements1[%s].a".formatted(LAST_INDEX));
        }

        @Test
        void nullDestinationElement_skipsAssignment() {
            final TargetSelector origin = elementOf(container).first().field(StringsAbc::getA);
            final TargetSelector destination = elementOf(container).last().field(StringsAbc::getA);

            final Object preBuilt = listOrArrayWithNullAt(LAST_INDEX);

            final Object result = Instancio.of(rootClass)
                    .set(container, preBuilt)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(StringsAbc.class, null, "abcElements1[%s]".formatted(LAST_INDEX));

            final List<StringsAbc> elements = getAbcElements1(result);
            assertThat(elements.get(0)).isNotNull();
            assertThat(elements.get(LAST_INDEX)).isNull();
        }

        private Object listOrArrayWithNullAt(final int nullIndex) {
            final StringsAbc nonNull = StringsAbc.builder().a("a").b("b").c("c").build();
            if (rootClass == AbcListHolder.class) {
                final List<StringsAbc> list = new ArrayList<>(SIZE);
                for (int i = 0; i < SIZE; i++) {
                    list.add(i == nullIndex ? null : nonNull);
                }
                return list;
            }
            final StringsAbc[] arr = new StringsAbc[SIZE];
            for (int i = 0; i < SIZE; i++) {
                arr[i] = i == nullIndex ? null : nonNull;
            }
            return arr;
        }


        private static Stream<TargetSelector> intermediateNullFieldPaths() {
            return Stream.of(
                    field(StringsDef::getE),  // path = [def, e]
                    field(StringsGhi::getG)); // path = [def, ghi, g]
        }

        @ParameterizedTest
        @MethodSource("intermediateNullFieldPaths")
        void intermediateNull_skipsAssignment(final TargetSelector elementField) {
            final TargetSelector origin = elementOf(container).first().target(elementField);
            final TargetSelector destination = elementOf(container).last().target(elementField);

            final Object result = Instancio.of(rootClass)
                    .set(field(StringsAbc::getDef), null)
                    // assignment is a no-op since def is null
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThatGraph(result)
                    .hasAllValuesOfTypeEqualTo(StringsDef.class, null);
        }
    }

    private static List<StringsAbc> getAbcElements1(final Object result) {
        return result instanceof AbcListHolder
                ? ((AbcListHolder) result).getAbcElements1()
                : Arrays.asList(((AbcArrayHolder) result).getAbcElements1());
    }

    private static List<StringsAbc> getAbcElements2(final Object result) {
        return result instanceof AbcListHolder
                ? ((AbcListHolder) result).getAbcElements2()
                : Arrays.asList(((AbcArrayHolder) result).getAbcElements2());
    }
}
