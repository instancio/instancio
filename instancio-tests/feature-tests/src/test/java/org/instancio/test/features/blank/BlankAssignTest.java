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
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;

@FeatureTag({Feature.BLANK, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class BlankAssignTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void copyValueOfNullField() {
        final StringsGhi result = Instancio.ofBlank(StringsGhi.class)
                .assign(valueOf(StringsGhi::getG).to(StringsGhi::getH))
                .create();

        assertThat(result.g).isEqualTo(result.h).isNull();
    }

    @Test
    void copyValueOfNonNullField() {
        final StringsGhi result = Instancio.ofBlank(StringsGhi.class)
                .generate(field(StringsGhi::getG), Generators::string)
                .assign(valueOf(StringsGhi::getG).to(StringsGhi::getH))
                .create();

        assertThat(result.g).isEqualTo(result.h).isNotBlank();
    }
}
