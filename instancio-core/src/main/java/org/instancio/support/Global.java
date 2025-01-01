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
package org.instancio.support;

import org.instancio.Random;
import org.instancio.documentation.InternalApi;
import org.instancio.internal.context.PropertiesLoader;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalApi
public final class Global {

    private static final Settings PROPERTIES_FILE_SETTINGS = Settings.defaults()
            .merge(Settings.from(PropertiesLoader.loadDefaultPropertiesFile()))
            .lock();

    private static final Random CONFIGURED_RANDOM = PROPERTIES_FILE_SETTINGS.get(Keys.SEED) == null
            ? null : new DefaultRandom(PROPERTIES_FILE_SETTINGS.get(Keys.SEED), Seeds.Source.GLOBAL);

    /**
     * Default settings overlaid with settings from {@code instancio.properties}.
     *
     * @return settings from properties file
     */
    @NotNull
    public static Settings getPropertiesFileSettings() {
        return PROPERTIES_FILE_SETTINGS;
    }

    @Nullable
    public static Random getConfiguredRandom() {
        return CONFIGURED_RANDOM;
    }

    private Global() {
        // non-instantiable
    }
}
