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
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.AFTER_GENERATE, Feature.GENERATOR, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class PopulateMapTest {

    private static @Data class StringsAbcMap {
        private final Map<StringsAbc, UUID> objectAsKeys = new LinkedHashMap<>();
        private final Map<UUID, StringsAbc> objectAsValues = new LinkedHashMap<>();
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldPopulateMapOfPojos(final AfterGenerate afterGenerate) {
        final StringsAbcMap objToPopulate = new StringsAbcMap();
        objToPopulate.objectAsKeys.put(StringsAbc.builder().a("A").build(), UUID.randomUUID());
        objToPopulate.objectAsKeys.put(StringsAbc.builder().b("B").build(), UUID.randomUUID());
        objToPopulate.objectAsValues.put(UUID.randomUUID(), StringsAbc.builder().a("A").build());
        objToPopulate.objectAsValues.put(UUID.randomUUID(), StringsAbc.builder().b("B").build());

        final Model<StringsAbcMap> model = PopulateHelper.populate(objToPopulate, afterGenerate);

        final StringsAbcMap result = Instancio.of(model)
                .set(field(StringsGhi::getH), "H")
                .create();

        assertThat(result).isSameAs(objToPopulate);

        assertThat(result.objectAsKeys.keySet())
                .hasSize(2)
                .first()
                .satisfies(element -> {
                    assertThat(element.getA()).isEqualTo("A");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThat(result.objectAsValues.values())
                .last()
                .satisfies(element -> {
                    assertThat(element.getB()).isEqualTo("B");
                    assertThat(element.getDef().getGhi().getH()).isEqualTo("H");
                });

        assertThatObject(result).isFullyPopulated();
    }

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldNotModifyNonPojoMap(final AfterGenerate afterGenerate) {
        final Map<String, Long> instance = new HashMap<>();
        instance.put("foo", 123L);

        final Model<Map<String, Long>> model = PopulateHelper.populate(instance, afterGenerate);

        final Map<String, Long> result = Instancio.of(model)
                .set(allStrings(), "bar")
                .lenient()
                .create();

        assertThat(result).isSameAs(instance).containsOnlyKeys("foo");

        assertThat(result.values())
                .first()
                .matches(val -> val > 0);
    }

}
