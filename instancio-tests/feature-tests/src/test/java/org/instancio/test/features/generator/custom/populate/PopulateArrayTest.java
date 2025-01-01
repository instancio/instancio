/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.AFTER_GENERATE, Feature.GENERATOR, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class PopulateArrayTest {

    private static @Data class StringsAbcArray {
        private StringsAbc[] array;
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldPopulateArrayOfPojos(final AfterGenerate afterGenerate) {
        final StringsAbc[] array = new StringsAbc[2];
        final StringsAbcArray objToPopulate = new StringsAbcArray();
        objToPopulate.array = array;
        objToPopulate.array[0] = StringsAbc.builder().a("A").build();
        objToPopulate.array[1] = StringsAbc.builder().b("B").build();

        final Model<StringsAbcArray> model = PopulateHelper.populate(objToPopulate, afterGenerate);

        final StringsAbcArray result = Instancio.of(model)
                .set(field(StringsGhi::getH), "H")
                .create();

        assertThat(result).isSameAs(objToPopulate);

        assertThat(result.array)
                .isSameAs(array)
                .hasSize(2);

        assertThat(result.array[0])
                .satisfies(element -> {
                    assertThat(element.getA()).isEqualTo("A");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThat(result.array[1])
                .satisfies(element -> {
                    assertThat(element.getB()).isEqualTo("B");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThatObject(result).isFullyPopulated();
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldNotModifyNonPojoArray(final AfterGenerate afterGenerate) {
        final String[] instance = {"foo"};

        final Model<String[]> model = PopulateHelper.populate(instance, afterGenerate);

        final String[] result = Instancio.of(model)
                .set(allStrings(), "bar")
                .create();

        assertThat(result).isSameAs(instance).containsOnly("bar");
    }

}
