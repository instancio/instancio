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
package org.instancio.test.features.values;

import org.instancio.Instancio;
import org.instancio.generator.specs.StringSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.VALUE_SPEC, Feature.WITH_SETTINGS_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class ValueSpecWithSettingsAnnotationSeedTest {

    private static final long EXPECTED_SEED = -1L;
    private static final String EXPECTED_VALUE = "VJFOW";

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SEED, EXPECTED_SEED);

    @Seed(EXPECTED_SEED)
    @Test
    void gen_shouldGenerateUsingAnnotationSeed() {
        final String result = Instancio.gen().string().get();

        assertThat(result).isEqualTo(EXPECTED_VALUE);
    }

    @Seed(EXPECTED_SEED)
    @Test
    void create_shouldGenerateUsingAnnotationSeed() {
        final String result = Instancio.create(String.class);

        assertThat(result).isEqualTo(EXPECTED_VALUE);
    }

    @Test
    void gen_withSettingsShouldTakePrecedenceOverWithSettingsAnnotation1() {
        final StringSpec spec = Instancio.gen()
                .withSettings(Settings.create().set(Keys.SEED, -999L))
                .string();

        final String s1 = spec.get();
        final String s2 = spec.get();

        // different strings should be generated
        // when reusing the same spec instance
        assertThat(s1).isNotEqualTo(s2);

        assertThat(s1).isNotEqualTo(EXPECTED_VALUE);
        assertThat(s2).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    void gen_withSettingsShouldTakePrecedenceOverWithSettingsAnnotation2() {
        final Supplier<String> supplier = () -> Instancio.gen()
                .withSettings(Settings.create().set(Keys.SEED, -999L))
                .string()
                .get();

        final String s1 = supplier.get();
        final String s2 = supplier.get();

        assertThat(s1).isEqualTo(s2);

        assertThat(s1).isNotEqualTo(EXPECTED_VALUE);
        assertThat(s2).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    void shouldGenerateTheSameValues1() {
        final String expected = Instancio.of(String.class)
                .withSeed(EXPECTED_SEED)
                .create();

        final String actual = Instancio.gen().string().get();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateTheSameValues2() {
        final String first = Instancio.gen().string().get();
        final String second = Instancio.gen().string().get();

        assertThat(first).isEqualTo(second);
    }
}
