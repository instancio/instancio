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
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.features.generator.custom.populate.PopulateHelper.populate;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.GENERATOR,
        Feature.MODEL,
        Feature.STREAM
})
@ExtendWith(InstancioExtension.class)
class PopulateStreamTest {

    /**
     * When given an object instance, {@code stream()}
     * will keep reusing the same object.
     */
    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void withObjectInstance(final AfterGenerate afterGenerate) {
        final StringsAbc objToPopulate = StringsAbc.builder().a("A").build();
        final Model<StringsAbc> model = PopulateHelper.populate(objToPopulate, afterGenerate);

        final Set<StringsAbc> results = Instancio.of(model)
                .set(field(StringsAbc::getB), "B")
                .set(field(StringsDef::getE), "E")
                .set(field(StringsGhi::getH), "H")
                .stream()
                .limit(5)
                .collect(Collectors.toSet());

        assertThat(results)
                .hasSize(1)
                .containsOnly(objToPopulate)
                .allSatisfy(PopulateStreamTest::assertResult);
    }

    /**
     * When given a supplier, {@code stream()} will
     * return different object instances.
     */
    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void withObjectSupplier(final AfterGenerate afterGenerate) {
        final Supplier<StringsAbc> supplier = () -> StringsAbc.builder().a("A").build();

        final Set<StringsAbc> results = Instancio.of(populate(supplier, afterGenerate))
                .set(field(StringsAbc::getB), "B")
                .set(field(StringsDef::getE), "E")
                .set(field(StringsGhi::getH), "H")
                .stream()
                .limit(5)
                .collect(Collectors.toSet());

        assertThat(results)
                .hasSize(5)
                .allSatisfy(PopulateStreamTest::assertResult);
    }

    private static void assertResult(final StringsAbc result) {
        assertThat(result.getA()).isEqualTo("A");
        assertThat(result.getB()).isEqualTo("B");
        assertThat(result.getDef().getE()).isEqualTo("E");
        assertThat(result.getDef().getGhi().getH()).isEqualTo("H");
        assertThatObject(result).isFullyPopulated();
    }
}
