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
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.generator.AfterGenerate;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringFields;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.fields;

@ExtendWith(InstancioExtension.class)
class InstancioExtensionWithSettingsAfterGenerateTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.POPULATE_NULLS);

    @Test
    void generatorShouldUseValueFromSettings() {
        final StringFields result = Instancio.of(StringFields.class)
                .supply(all(StringFields.class), random -> StringFields.builder().one("one").build())
                .set(fields().annotated(StringFields.Two.class), "two")
                .withSettings(Settings.create()
                        .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.POPULATE_NULLS))
                .create();

        assertThat(result.getOne()).isEqualTo("one");
        assertThat(result.getTwo()).isEqualTo("two");
        // Should populate nulls
        assertThat(result.getThree()).isNotBlank();
        assertThat(result.getFour()).isNotBlank();
    }
}
