/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.properties;

import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seed precedence when NOT using {@code InstancioExtension}, from highest to lowest:
 *
 * <ul>
 *   <li>(a) withSeed()</li>
 *   <li>(b) withSettings()</li>
 *   <li>(c) Global seed (from properties file)</li>
 *   <li>(d) random seed, if none of the above specified (default behaviour, not tested here)</li>
 * </ul>
 */
@FeatureTag({Feature.GLOBAL_SEED, Feature.WITH_SEED})
class GlobalSeedTest {

    @Test
    @DisplayName("(a) withSeed() takes precedence over everything else")
    void withSeedTakesPrecedenceOverGlobalSeed() {
        final long seed = Instancio.create(long.class);
        final Result<String> s1 = Instancio.of(String.class).withSeed(seed).asResult();
        final Result<String> s2 = Instancio.of(String.class).withSeed(seed).asResult();
        final Result<String> s3 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(seed);
        assertThat(s3.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

        assertThat(s1.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s2.get());

        assertThat(s1.get()).isNotEqualTo(s3.get());
    }

    @Test
    @DisplayName("(b) withSettings()")
    void settingsSeedTakesPrecedenceOverGlobalSeed() {
        final long seed = Instancio.create(long.class);
        final Settings settings = Settings.create().set(Keys.SEED, seed);
        final Result<String> s1 = Instancio.of(String.class).withSettings(settings).asResult();
        final Result<String> s2 = Instancio.of(String.class).withSettings(settings).asResult();
        final Result<String> s3 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(seed);
        assertThat(s3.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

        assertThat(s1.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s2.get());

        assertThat(s1.get()).isNotEqualTo(s3.get());
    }

    @Test
    @DisplayName("(c) Global seed from properties file")
    void seedFromProperties() {
        final Result<String> s1 = Instancio.of(String.class).asResult();
        final Result<String> s2 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);
        assertThat(s2.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

        assertThat(s1.get())
                .as("Distinct values should be generated")
                .isNotEqualTo(s2.get());
    }

}
