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

import org.instancio.ConvertibleToScope;
import org.instancio.IndexedElementSelector;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@SuppressWarnings("NullAway")
@ParameterizedClass
@MethodSource("containers")
@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.SELECTOR, Feature.SCOPE})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorScopeTest {

    private static final String EXPECTED_STRING = "_value_";
    private static final StringsAbc EXPECTED_ABC = new StringsAbc();
    private static final int[] INDICES = {0, 3, 7};
    private static final int SIZE = 8;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Parameter(0)
    private Class<?> rootClass;

    @Parameter(1)
    private IndexedElementSelector indexedElementSelector;

    private Class<?> getNestedClass() {
        return rootClass == AbcListHolder.class
                ? AbcListHolder.Nested.class
                : AbcArrayHolder.Nested.class;
    }

    // Select specific indices across all collections/arrays within the object graph.
    // Then narrow down using scope on element's targets.
    static Stream<Arguments> containers() {
        final Stream<Arguments> listRows = Stream.of(
                elementOf(all(List.class)).at(INDICES),
                elementOf(types().of(List.class)).at(INDICES),
                elementOf(fields().ofType(List.class)).at(INDICES),
                elementOf(fields().matching("abcElements.*")).at(INDICES),
                elementOf(types().of(List.class).within(scope(AbcListHolder.class))).at(INDICES),
                elementOf(types().of(List.class).atDepth(d -> d > 0).within(scope(AbcListHolder.class))).at(INDICES)
        ).map(s -> Arguments.of(AbcListHolder.class, s));

        final Stream<Arguments> arrayRows = Stream.of(
                elementOf(all(StringsAbc[].class)).at(INDICES),
                elementOf(types(Class::isArray)).at(INDICES),
                elementOf(fields().ofType(StringsAbc[].class)).at(INDICES),
                elementOf(fields().matching("abcElements.*")).at(INDICES),
                elementOf(types(Class::isArray).within(scope(AbcArrayHolder.class))).at(INDICES),
                elementOf(types(Class::isArray).atDepth(d -> d > 0).within(scope(AbcArrayHolder.class))).at(INDICES)
        ).map(s -> Arguments.of(AbcArrayHolder.class, s));

        return Stream.concat(listRows, arrayRows);
    }

    /**
     * Target {@link StringsAbc} elements within:
     *
     * <p>{@code <root> -> Nested -> abcElements2 -> StringsAbc}
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Target_StringsAbc_Within_Nested_abcElements2 {

        private final List<TargetSelector> targetSelectors = List.of(
                all(StringsAbc.class).within(scope(getNestedClass(), "abcElements2")));

        @ParameterizedTest
        @FieldSource("targetSelectors")
        void target(final TargetSelector elementTarget) {
            final var result = Instancio.of(rootClass)
                    .set(indexedElementSelector.target(elementTarget), EXPECTED_ABC)
                    .create();

            final String subtree = "nested.abcElements2[0,3,7]";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtree);
        }
    }

    /**
     * Target strings:
     *
     * <p>{@code <root> -> abcElements1 -> StringsAbc -> StringsDef -> StringsGhi -> all strings}
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Target_AllStrings_Within_abcElements1_StringsGhi {

        private final List<TargetSelector> targetSelectors = List.of(
                allStrings().within(
                        scope(rootClass, "abcElements1"),
                        scope(StringsGhi.class)),

                fields().ofType(String.class).within(
                        scope(rootClass, "abcElements1"),
                        scope(StringsGhi.class)),

                types().of(String.class).within(
                        scope(rootClass, "abcElements1"),
                        fields().declaredIn(StringsGhi.class).toScope()),

                allStrings().within(
                        fields().named("abcElements1").declaredIn(rootClass).toScope(),
                        fields().matching("g|h|i").atDepth(5).toScope()));

        @ParameterizedTest
        @FieldSource("targetSelectors")
        void target(final TargetSelector elementTarget) {
            final var result = Instancio.of(rootClass)
                    .set(indexedElementSelector.target(elementTarget), EXPECTED_STRING)
                    .create();

            final String subtree = "abcElements1[0,3,7].def.ghi";

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtree);
        }
    }

    /**
     * Target strings:
     *
     * <p>{@code <root> -> [abcElements1 + abcElements2] -> StringsAbc -> StringsDef -> StringsGhi -> all strings}
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Target_AllStrings_Within_abcElements1_and_abcElements2_StringsGhi {

        private final List<TargetSelector> targetSelectors = List.of(
                allStrings().within(
                        fields().declaredIn(rootClass).matching("abcElements1|abcElements2").toScope(),
                        scope(StringsGhi.class)),

                fields().ofType(String.class).within(
                        fields().declaredIn(rootClass).matching("abcElements.*").toScope(),
                        scope(StringsGhi.class)),

                fields().ofType(String.class).within(
                        fields().matching("abcElements1|abcElements2").toScope(),
                        types().of(StringsGhi.class).atDepth(4).toScope())
        );

        @ParameterizedTest
        @FieldSource("targetSelectors")
        void target(final TargetSelector elementTarget) {
            final var result = Instancio.of(rootClass)
                    .set(indexedElementSelector.target(elementTarget), EXPECTED_STRING)
                    .create();

            final String[] subtrees = {
                    "abcElements1[0,3,7].def.ghi",
                    "abcElements2[0,3,7].def.ghi"
            };

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtrees);
        }
    }

    /**
     * Target strings:
     *
     * <p>{@code <root> -> Nested -> [abcElements1 + abcElements2] -> StringsAbc -> StringsDef -> StringsGhi -> all strings}
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Target_AllStrings_Within_Nested_abcElements1_and_abcElements2_StringsGhi {

        private final List<TargetSelector> targetSelectors = List.of(
                allStrings().within(
                        scope(getNestedClass()),
                        scope(StringsGhi.class)),

                allStrings().within(
                        fields().declaredIn(getNestedClass()).toScope(),
                        scope(StringsGhi.class)),

                allStrings().within(
                        fields().declaredIn(getNestedClass()).matching("abcElements1|abcElements2").toScope(),
                        scope(StringsGhi.class)),

                allStrings().within(
                        scope(rootClass),
                        fields().declaredIn(getNestedClass()).matching("abcElements1|abcElements2").toScope(),
                        scope(StringsGhi.class)),

                fields().ofType(String.class).within(
                        fields().declaredIn(getNestedClass()).matching("abcElements.*").toScope(),
                        scope(StringsGhi.class)),

                fields().ofType(String.class).within(
                        fields().matching("abcElements1|abcElements2").toScope(),
                        types().of(StringsGhi.class).atDepth(5).toScope())
        );

        @ParameterizedTest
        @FieldSource("targetSelectors")
        void target(final TargetSelector elementTarget) {
            final var result = Instancio.of(rootClass)
                    .set(indexedElementSelector.target(elementTarget), EXPECTED_STRING)
                    .create();

            final String[] subtrees = {
                    "nested.abcElements1[0,3,7].def.ghi",
                    "nested.abcElements2[0,3,7].def.ghi"
            };

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtrees);
        }
    }

    /**
     * Target {@link StringsAbc#getB()} within all collections/arrays.
     *
     * <p>{@code <root> -> [abcElements1 + abcElements2] -> StringsAbc -> b}
     * <p>{@code <root> -> Nested -> [abcElements1 + abcElements2] -> StringsAbc -> b}
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Target_AllFieldsB_InAllElements {

        final ConvertibleToScope containerTypes = rootClass == AbcListHolder.class
                ? all(List.class)
                : types(Class::isArray);

        private final List<TargetSelector> targetSelectors = List.of(
                field(StringsAbc::getB),
                field(StringsAbc::getB).within(containerTypes.toScope()),
                fields().named("b"),
                fields().named("b").within(containerTypes.toScope())
        );

        @ParameterizedTest
        @FieldSource("targetSelectors")
        void target(final TargetSelector elementTarget) {
            final var result = Instancio.of(rootClass)
                    .set(indexedElementSelector.target(elementTarget), EXPECTED_STRING)
                    .create();

            final String[] subtrees = {
                    "**.abcElements1[0,3,7].b",
                    "**.abcElements2[0,3,7].b"
            };

            assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtrees);
        }
    }
}
