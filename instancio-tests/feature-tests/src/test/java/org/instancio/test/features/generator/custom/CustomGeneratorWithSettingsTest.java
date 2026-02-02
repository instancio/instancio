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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.fields;

@FeatureTag({Feature.GENERATOR, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class CustomGeneratorWithSettingsTest {

    private static final String OVERRIDE_TWO = "override-two";
    private static final String ONE = "one";

    private static class StringFieldsGenerator implements Generator<StringFields> {
        private final Hints hints;

        private StringFieldsGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public StringFields generate(final Random random) {
            // remaining fields are null
            return StringFields.builder().one(ONE).build();
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    private static final Generator<?> GENERATOR_WITH_NULL_HINTS = new StringFieldsGenerator(null);

    @Nested
    class GeneratorWithNullHintsTest {

        @Test
        @DisplayName("Default settings: AfterGenerate should default to NULLS_AND_DEFAULT_PRIMITIVES")
        void withDefaultSettings() {
            final StringFields result = Instancio.of(StringFields.class)
                    .supply(all(StringFields.class), GENERATOR_WITH_NULL_HINTS)
                    .set(fields().annotated(StringFields.Two.class), OVERRIDE_TWO)
                    .create();

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isNotNull();
            assertThat(result.getFour()).isNotNull();
        }

        @Test
        @DisplayName("Settings: POPULATE_NULLS")
        void settingIsPopulateNulls() {
            final StringFields result = Instancio.of(StringFields.class)
                    .supply(all(StringFields.class), GENERATOR_WITH_NULL_HINTS)
                    .set(fields().annotated(StringFields.Two.class), OVERRIDE_TWO)
                    .withSettings(Settings.create()
                            .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.POPULATE_NULLS))
                    .create();

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isNotNull();
            assertThat(result.getFour()).isNotNull();
        }

        @Test
        @DisplayName("Settings: DO_NOT_MODIFY")
        void settingIsDoNotModify() {
            final StringFields result = Instancio.of(StringFields.class)
                    .supply(all(StringFields.class), GENERATOR_WITH_NULL_HINTS)
                    .set(fields().annotated(StringFields.Two.class), OVERRIDE_TWO)
                    .withSettings(Settings.create()
                            .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.DO_NOT_MODIFY))
                    .lenient()
                    .create();

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isNull();
            assertThat(result.getThree()).isNull();
            assertThat(result.getFour()).isNull();
        }
    }

    @Nested
    class GeneratorWithPopulateNullsHintTest {

        @Test
        @DisplayName("AfterGenerate from Generator.hints() take precedence over Settings")
        void settingsIsPopulateNulls() {
            final Generator<?> generator = new StringFieldsGenerator(
                    Hints.afterGenerate(AfterGenerate.POPULATE_NULLS));

            final StringFields result = Instancio.of(StringFields.class)
                    .supply(all(StringFields.class), generator)
                    .set(fields().annotated(StringFields.Two.class), OVERRIDE_TWO)
                    .withSettings(Settings.create()
                            .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.DO_NOT_MODIFY))
                    .create();

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isNotNull();
            assertThat(result.getFour()).isNotNull();
        }
    }

}
