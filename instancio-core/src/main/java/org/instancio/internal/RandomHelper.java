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
package org.instancio.internal;

import org.instancio.Random;
import org.instancio.documentation.InternalApi;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Global;
import org.instancio.support.Seeds;
import org.instancio.support.ThreadLocalRandom;
import org.jetbrains.annotations.Nullable;

@InternalApi
public final class RandomHelper {

    /**
     * Precedence of supplied seed values.
     *
     * <ol>
     *   <li>{@code withSeed(long)}</li>
     *   <li>{@code withSettings(Settings)}</li>
     *   <li>{@code @WithSettings Settings}</li>
     *   <li>{@code @Seed(long)}</li>
     *   <li>{@code instancio.properties}</li>
     *   <li>random seed</li>
     * </ol>
     *
     * @param settingsSeed seed from {@code Settings}
     * @param withSeed     seed from {@code withSeed()}
     * @return random instance resolved using the above precedence rules
     */
    public static Random resolveRandom(
            @Nullable final Long settingsSeed,
            @Nullable final Long withSeed) {

        if (withSeed != null) {
            return new DefaultRandom(withSeed, Seeds.Source.MANUAL);
        }

        // Based on instancio.properties seed, if defined
        final Random configuredRandom = Global.getConfiguredRandom();

        // This ensures we can override seed from the properties file using a custom Settings instance.
        if (settingsSeed != null && (configuredRandom == null || configuredRandom.getSeed() != settingsSeed)) {
            return new DefaultRandom(settingsSeed, Seeds.Source.WITH_SETTINGS_BUILDER);
        }

        // If running under JUnit extension, use the Random instance supplied by the extension
        if (ThreadLocalRandom.getInstance().get() != null) {
            return ThreadLocalRandom.getInstance().get();
        }

        if (configuredRandom != null) {
            return configuredRandom;
        }

        // Random seed
        return new DefaultRandom();
    }

    private RandomHelper() {
        // non-instantiable
    }
}
