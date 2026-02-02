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
package org.instancio.test.features.fill;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.FILL)
@ExtendWith(InstancioExtension.class)
class FillCollectionTest {

    private static Stream<Arguments> collectionInstance() {
        return Stream.of(
                Arguments.of(new ArrayList<>(), FillType.POPULATE_NULLS),
                Arguments.of(new ArrayList<>(), FillType.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES),
                Arguments.of(new LinkedHashSet<>(), FillType.POPULATE_NULLS),
                Arguments.of(new LinkedHashSet<>(), FillType.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));
    }

    private static @Data class StringsAbcCollection {
        private Collection<StringsAbc> collection;
    }

    @MethodSource("collectionInstance")
    @ParameterizedTest
    void shouldPopulateCollectionOfPojos(
            final Collection<StringsAbc> collection,
            final FillType fillType) {

        final StringsAbcCollection object = new StringsAbcCollection();
        object.collection = collection;
        object.collection.add(StringsAbc.builder().a("A").build());
        object.collection.add(StringsAbc.builder().b("B").build());

        Instancio.ofObject(object)
                .set(field(StringsGhi::getH), "H")
                .withFillType(fillType)
                .fill();

        assertThatObject(object).isFullyPopulated();

        assertThat(object.collection)
                .isSameAs(collection)
                .hasSize(2)
                .first()
                .satisfies(element -> {
                    assertThat(element.getA()).isEqualTo("A");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThat(object.collection)
                .last()
                .satisfies(element -> {
                    assertThat(element.getB()).isEqualTo("B");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });
    }

    @EnumSource(FillType.class)
    @ParameterizedTest
    void overwriteCollectionField(final FillType fillType) {
        final ArrayList<StringsAbc> collection = new ArrayList<>();
        final StringsAbcCollection object = new StringsAbcCollection();
        object.collection = collection;
        object.collection.add(StringsAbc.builder().a("A").build());
        object.collection.add(StringsAbc.builder().b("B").build());

        Instancio.ofObject(object)
                .withFillType(fillType)
                .set(field(StringsGhi::getH), "H")
                .generate(field("collection"), gen -> gen.collection().size(4))
                .fill();

        assertThat(object.collection)
                .isNotSameAs(collection)
                .hasSize(4)
                .allSatisfy(element -> {
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                    // new collection elements should have random values
                    assertThat(element.getA()).isNotBlank().isNotEqualTo("A");
                    assertThat(element.getB()).isNotBlank().isNotEqualTo("B");
                });
    }

    /**
     * Note: null collection elements are not populated.
     */
    @EnumSource(FillType.class)
    @ParameterizedTest
    void shouldNotReplaceExistingCollectionElements(final FillType fillType) {
        final String[] initialValues = {"foo", null};
        final List<String> object = CollectionUtils.asArrayList(initialValues);

        Instancio.ofObject(object)
                .withFillType(fillType)
                .set(allStrings().lenient(), "bar")
                .fill();

        assertThat(object).containsOnly(initialValues);
    }

    @Nested
    class CollectionWithNullsTest {

        @Test
        void collectionWithOnlyNullElement() {
            final Collection<String> object = new ArrayList<>();
            object.add(null);

            assertThatThrownBy(() -> Instancio.fill(object))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("the fill() method cannot resolve type arguments" +
                            " from the given ArrayList instance because it contains null value(s)");
        }

        @Test
        void collectionWithSingleNonNullElement() {
            final Collection<String> object = new ArrayList<>();
            object.add("test");

            Instancio.fill(object);

            assertThat(object)
                    .hasSize(1)
                    .containsOnly("test");
        }

        @Test
        void collectionWithMixedElements() {
            final Collection<String> object = new ArrayList<>();
            object.add(null);
            object.add("test");
            object.add(null);

            Instancio.fill(object);

            assertThat(object)
                    .hasSize(3)
                    .containsExactly(null, "test", null);
        }

        @Test
        void populateHashSet() {
            final Collection<Integer> object = new HashSet<>();
            object.add(42);

            Instancio.fill(object);

            assertThat(object).containsOnly(42);
        }
    }
}
