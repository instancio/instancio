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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
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
import static org.instancio.Select.fields;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.BLANK, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class BlankSetModelTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void setBlankModel_toNonBlankObject() {
        final Model<StringsDef> blanknModel = Instancio.ofBlank(StringsDef.class)
                .set(field(StringsGhi::getH), "H")
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), blanknModel)
                .create();

        assertThat(result.a).isNotBlank();
        assertThat(result.b).isNotBlank();
        assertThat(result.c).isNotBlank();
        assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
        assertThat(result.def.ghi.g).isNull();
        assertThat(result.def.ghi.h).isEqualTo("H");
        assertThat(result.def.ghi.i).isNull();
    }

    @Test
    void setBlankModel_toBlankObject() {
        final Model<StringsDef> blankModel = Instancio.ofBlank(StringsDef.class)
                .set(fields().named("h"), "H")
                .toModel();

        // set blank model on blank object
        final StringsAbc result = Instancio.ofBlank(StringsAbc.class)
                .setModel(all(StringsDef.class), blankModel)
                .create();

        assertThatObject(result).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
        assertThat(result.def.ghi.g).isNull();
        assertThat(result.def.ghi.h).isEqualTo("H");
        assertThat(result.def.ghi.i).isNull();
    }
}
