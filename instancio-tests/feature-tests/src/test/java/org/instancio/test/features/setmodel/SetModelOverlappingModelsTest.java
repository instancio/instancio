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
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class SetModelOverlappingModelsTest {

    @Test
    void overlappingModels1() {
        final Model<StringsGhi> ghiModel = Instancio.of(StringsGhi.class)
                .set(field(StringsGhi::getH), "h")
                .toModel();

        // Note: StringsDef contains StringsGhi
        final Model<StringsDef> defModel = Instancio.of(StringsDef.class)
                .set(field(StringsDef::getE), "e")
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), defModel)
                .setModel(all(StringsGhi.class), ghiModel)
                .create();

        assertThat(result.getDef().getE()).isEqualTo("e");
        assertThat(result.getDef().getGhi().getH()).isEqualTo("h");
    }

    /**
     * Calling {@code setModel()} with the same model twice.
     */
    @Test
    void overlappingModels() {
        // must be lenient to avoid unused selector error
        final TargetSelector getH = field(StringsGhi::getH).lenient();

        final Model<StringsGhi> ghiModel = Instancio.of(StringsGhi.class)
                .set(getH, "h")
                .toModel();

        final Model<StringsDef> defModel = Instancio.of(StringsDef.class)
                .set(field(StringsDef::getE), "e")
                .setModel(all(StringsGhi.class), ghiModel) // ghi model 1
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), defModel)
                .setModel(all(StringsGhi.class), ghiModel)  // ghi model 2
                .create();

        assertThat(result.getDef().getE()).isEqualTo("e");
        assertThat(result.getDef().getGhi().getH()).isEqualTo("h");
    }

}
