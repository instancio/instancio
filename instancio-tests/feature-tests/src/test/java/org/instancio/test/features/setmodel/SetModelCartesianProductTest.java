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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.CARTESIAN_PRODUCT})
@ExtendWith(InstancioExtension.class)
class SetModelCartesianProductTest {

    @Test
    void cartesianProduct() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .ignore(all(StringsGhi.class))
                .set(field(StringsDef::getD), "d")
                .toModel();

        final List<StringsAbc> results = Instancio.ofCartesianProduct(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .with(field(StringsAbc::getA), "a")
                .create();

        assertThat(results).hasSize(1).allSatisfy(result -> {
            assertThat(result.getA()).isEqualTo("a");
            assertThat(result.getDef().getD()).isEqualTo("d");
            assertThat(result.getDef().getGhi()).isNull();
        });
    }
}
