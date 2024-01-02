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
package org.instancio.generator;

import org.instancio.Random;
import org.instancio.settings.Settings;

/**
 * Provides additional information, such as settings and a random
 * instance to generators that require it.
 *
 * @see Generator
 * @since 1.0.3
 */
public final class GeneratorContext {

    private final Settings settings;
    private final Random random;

    public GeneratorContext(final Settings settings, final Random random) {
        this.settings = settings;
        this.random = random;
    }

    /**
     * Returns a read-only instance of the settings used by Instancio.
     * <p>
     * The returned settings includes overrides specified using
     *
     * <ul>
     *   <li>{@link org.instancio.InstancioApi#withSettings(Settings)}</li>
     *   <li>{@code @WithSettings} annotation used with {@code InstancioExtension} for JUnit 5</li>
     * </ul>
     *
     * @return the settings
     * @since 1.0.3
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Returns the random instance used by Instancio to generate data.
     * <p>
     * Using this instance ensures that data will be reproducible.
     *
     * @return the random instance
     * @since 1.0.3
     */
    public Random random() {
        return random;
    }
}
