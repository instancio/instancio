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
package org.instancio.test.features.stream;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({
        Feature.SETTINGS,
        Feature.STREAM,
        Feature.WITH_SETTINGS_ANNOTATION
})
@ExtendWith(InstancioExtension.class)
class CreateStreamWithSettingsTest {

    private static final int LIMIT = 100;
    private static final int STRING_LENGTH = 50;

    @WithSettings
    private final Settings settings = createSettingsWithStringLength(STRING_LENGTH);

    private static Settings createSettingsWithStringLength(final int length) {
        return Settings.create()
                .set(Keys.STRING_MIN_LENGTH, length)
                .set(Keys.STRING_MAX_LENGTH, length)
                .lock();
    }

    @Test
    void streamShouldUseInjectedSettings() {
        final Stream<String> results = Instancio.stream(String.class).limit(LIMIT);

        assertThat(results)
                .hasSize(LIMIT)
                .allMatch(s -> s.length() == STRING_LENGTH);
    }

    @Test
    void overrideInjectedSettings() {
        final int overriddenLength = Instancio.create(int.class);
        final Settings overrides = createSettingsWithStringLength(overriddenLength);

        final Stream<String> results = Instancio.of(String.class)
                .withSettings(overrides)
                .stream().limit(LIMIT);

        assertThat(results)
                .hasSize(LIMIT)
                .allMatch(s -> s.length() == overriddenLength);
    }

    @Test
    void streamOfModelWithInjectedSettings() {
        final Model<StringHolder> model = Instancio.of(StringHolder.class).toModel();

        final Stream<StringHolder> results = Instancio.stream(model).limit(LIMIT);

        assertThat(results)
                .hasSize(LIMIT)
                .allSatisfy(result -> assertThat(result.getValue()).hasSize(STRING_LENGTH));
    }

    @Test
    @DisplayName("The model overrides injected settings")
    void streamOfModelWithOverriddenSettings() {
        final int overriddenLength = Instancio.create(int.class);
        final Settings overrides = createSettingsWithStringLength(overriddenLength);

        final Model<StringHolder> model = Instancio.of(StringHolder.class)
                .withSettings(overrides)
                .toModel();

        final Stream<StringHolder> results = Instancio.stream(model).limit(LIMIT);

        assertThat(results)
                .hasSize(LIMIT)
                .allSatisfy(result -> assertThat(result.getValue()).hasSize(overriddenLength));
    }
}
