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
package org.instancio.support;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.context.PropertiesLoader;
import org.instancio.internal.settings.InternalSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@InternalApi
public final class Global {

    private static final PropertiesLoader.LoadedProperties PROPERTIES = PropertiesLoader.load();

    private static final Settings GLOBAL_SETTINGS = InternalSettings.getLockedDefaults()
            .merge(Settings.from(PROPERTIES.properties()))
            .lock();

    private static final @Nullable Long GLOBAL_SEED = GLOBAL_SETTINGS.get(Keys.SEED);
    private static final @Nullable DefaultRandom CONFIGURED_RANDOM = GLOBAL_SEED == null
            ? null : new DefaultRandom(GLOBAL_SEED, new Seeds.Source(requireNonNull(PROPERTIES.seedSource())));

    /**
     * Default settings overlaid with settings from {@code instancio.properties}
     * (or the file specified by {@code instancio.config.file}),
     * {@code INSTANCIO_*} environment variables and {@code instancio.*} system properties.
     *
     * @return global settings
     */
    public static Settings getGlobalSettings() {
        return GLOBAL_SETTINGS;
    }

    /**
     * Resolves effective settings by layering:
     * global settings, thread-local, and {@code overrides}.
     *
     * @param overrides optional overrides with the highest priority
     * @return merged (possibly locked) settings
     */
    public static Settings resolveEffectiveSettings(@Nullable final Settings overrides) {
        final InternalTestContext internalTestContext = ThreadLocalTestContext.getInstance().get();
        final Settings threadLocalSettings = internalTestContext == null ? null : internalTestContext.getSettings();
        if (threadLocalSettings == null && overrides == null) {
            return GLOBAL_SETTINGS; // locked instance
        }
        return InternalSettings.from(GLOBAL_SETTINGS)
                .copyFrom(threadLocalSettings)
                .copyFrom(overrides);
    }

    @Nullable
    public static DefaultRandom getConfiguredRandom() {
        return CONFIGURED_RANDOM;
    }

    private Global() {
        // non-instantiable
    }
}
