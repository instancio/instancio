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
package org.instancio.test.globalsettings;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.settings.StringType;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalTestContext;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Settings are supplied by the surefire configuration in this module's pom.
 */
@FeatureTag({Feature.SETTINGS, Feature.GLOBAL_SEED})
@ExtendWith(InstancioExtension.class)
class GlobalSettingsTest {

    private final Settings settings = Global.getGlobalSettings();

    @Test
    void configFileReplacesDefaultPropertiesFile() {
        assertThat(settings.get(Keys.STRING_TYPE)).isEqualTo(StringType.DIGITS);
        assertThat(settings.get(Keys.BOOLEAN_NULLABLE)).isFalse();
        assertThat(Instancio.create(new TypeToken<List<String>>() {})).isExactlyInstanceOf(LinkedList.class);
    }

    @Test
    void environmentVariableOverridesConfigFile() {
        assertThat(settings.get(Keys.INTEGER_MAX)).isEqualTo(200);
    }

    @Test
    void customKeyFromEnvironmentVariable() {
        final SettingKey<Integer> key = Keys.ofType(Integer.class, 0)
                .withPropertyKey("my.retry.count")
                .create();

        assertThat(settings.get(key)).isEqualTo(5);
    }

    @Test
    void customKeyFromSystemProperty() {
        // Mixed case and an underscore, which an environment variable can't express
        final SettingKey<Integer> key = Keys.ofType(Integer.class, 0)
                .withPropertyKey("myApp.retry_count")
                .create();

        assertThat(settings.get(key)).isEqualTo(3);
    }

    @Test
    void subtypeFromSystemProperty() {
        assertThat(Instancio.create(new TypeToken<Collection<String>>() {})).isExactlyInstanceOf(ArrayDeque.class);
    }

    @Test
    void systemPropertyOverridesEnvironmentVariable() {
        final DefaultRandom random = requireNonNull(ThreadLocalTestContext.getInstance().get()).getRandom();

        assertThat(random.getSeed()).isEqualTo(3);
        assertThat(random.getSource().description()).isEqualTo("-Dinstancio.seed");
        assertThat(Instancio.of(String.class).asResult().getSeed()).isEqualTo(3);
    }
}
