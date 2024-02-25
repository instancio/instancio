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
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.features.generator.custom.populate.PopulateHelper.populate;

@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.GENERATOR,
        Feature.MODEL,
        Feature.WITH_NULLABLE
})
@ExtendWith(InstancioExtension.class)
class PopulateWithNullableTest {

    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void withNullableField(final AfterGenerate afterGenerate) {
        final Supplier<StringsAbc> supplier = () -> StringsAbc.builder()
                .a("A")
                .def(StringsDef.builder()
                        .d("D")
                        .build())
                .build();

        final Model<StringsAbc> model = populate(supplier, afterGenerate);

        final Stream<StringsAbc> results = Instancio.of(model)
                .withNullable(all(
                        field(StringsDef::getE),
                        field(StringsGhi::getI),
                        all(StringsGhi.class)))
                .stream()
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(results)
                .allMatch(res -> res.getA().equals("A"))
                .allMatch(res -> res.getDef().getD().equals("D"))
                .anyMatch(res -> res.getDef().getE() == null)
                .anyMatch(res -> res.getDef().getGhi() == null)
                .anyMatch(res -> res.getDef().getGhi() != null && res.getDef().getGhi().getI() == null);
    }
}
