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
package org.instancio.test.features.values;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.VALUE_SPEC, Feature.WITH_SETTINGS_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class ValueSpecWithSettingsAnnotationSeedTest {

    private static final long EXPECTED_SEED = -1L;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SEED, EXPECTED_SEED);

    @Test
    void shouldBeGeneratedUsingExpectedSeed() {
        final String expected = Instancio.of(String.class)
                .withSeed(EXPECTED_SEED)
                .create();

        final String actual = Gen.string().get();

        assertThat(actual).isEqualTo(expected);
    }
}
