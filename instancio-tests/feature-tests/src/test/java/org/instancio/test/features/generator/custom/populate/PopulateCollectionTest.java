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
package org.instancio.test.features.generator.custom.populate;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.generator.AfterGenerate;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.features.generator.custom.populate.PopulateHelper.populate;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.AFTER_GENERATE, Feature.GENERATOR, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class PopulateCollectionTest {

    private static Stream<Arguments> collectionInstance() {
        return Stream.of(
                Arguments.of(new ArrayList<>(), AfterGenerate.POPULATE_NULLS),
                Arguments.of(new ArrayList<>(), AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES),
                Arguments.of(new LinkedHashSet<>(), AfterGenerate.POPULATE_NULLS),
                Arguments.of(new LinkedHashSet<>(), AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES));
    }

    private static @Data class StringsAbcCollection {
        private Collection<StringsAbc> collection;
    }

    @MethodSource("collectionInstance")
    @ParameterizedTest
    void shouldPopulateCollectionOfPojos(
            final Collection<StringsAbc> collection,
            final AfterGenerate afterGenerate) {

        final StringsAbcCollection objToPopulate = new StringsAbcCollection();
        objToPopulate.collection = collection;
        objToPopulate.collection.add(StringsAbc.builder().a("A").build());
        objToPopulate.collection.add(StringsAbc.builder().b("B").build());

        final Model<StringsAbcCollection> model = PopulateHelper.populate(objToPopulate, afterGenerate);

        final StringsAbcCollection result = Instancio.of(model)
                .set(field(StringsGhi::getH), "H")
                .create();

        assertThat(result).isSameAs(objToPopulate);

        assertThat(result.collection)
                .isSameAs(collection)
                .hasSize(2)
                .first()
                .satisfies(element -> {
                    assertThat(element.getA()).isEqualTo("A");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThat(result.collection)
                .last()
                .satisfies(element -> {
                    assertThat(element.getB()).isEqualTo("B");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThatObject(result).isFullyPopulated();
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void overwriteCollectionField(final AfterGenerate afterGenerate) {
        final ArrayList<StringsAbc> collection = new ArrayList<>();
        final StringsAbcCollection objToPopulate = new StringsAbcCollection();
        objToPopulate.collection = collection;
        objToPopulate.collection.add(StringsAbc.builder().a("A").build());
        objToPopulate.collection.add(StringsAbc.builder().b("B").build());

        final Model<StringsAbcCollection> model = PopulateHelper.populate(objToPopulate, afterGenerate);

        final StringsAbcCollection result = Instancio.of(model)
                .set(field(StringsGhi::getH), "H")
                .generate(field("collection"), gen -> gen.collection().size(4))
                .create();

        assertThat(result).isSameAs(objToPopulate);
        assertThat(result.collection)
                .isNotSameAs(collection)
                .hasSize(4)
                .allSatisfy(element -> {
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                    // new collection elements should have random values
                    assertThat(element.getA()).isNotBlank().isNotEqualTo("A");
                    assertThat(element.getB()).isNotBlank().isNotEqualTo("B");
                });
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldNotModifyNonPojoCollection(final AfterGenerate afterGenerate) {
        final List<String> listToPopulate = Collections.unmodifiableList(Arrays.asList("foo"));

        final Model<List<String>> model = PopulateHelper.populate(listToPopulate, afterGenerate);

        final List<String> result = Instancio.of(model)
                .set(allStrings(), "bar")
                .lenient()
                .create();

        assertThat(result).isSameAs(listToPopulate).containsOnly("foo");
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void ofSet(final AfterGenerate afterGenerate) {
        final Supplier<StringsAbc> supplier = () -> StringsAbc.builder()
                .a("A")
                .def(StringsDef.builder().build())
                .build();

        final Model<StringsAbc> model = populate(supplier, afterGenerate);

        final Set<StringsAbc> results = Instancio.ofSet(model)
                .size(5)
                .set(field(StringsDef::getE), "E")
                .set(field(StringsGhi::getI), "I")
                .create();

        assertThat(results).hasSize(5).allSatisfy(result -> {
            assertThat(result.getA()).isEqualTo("A");
            assertThat(result.getDef().getE()).isEqualTo("E");
            assertThat(result.getDef().getGhi().getI()).isEqualTo("I");
        });

        assertThatObject(results).isFullyPopulated();
    }
}
