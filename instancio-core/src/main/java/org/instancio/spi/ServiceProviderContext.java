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
package org.instancio.spi;

import org.instancio.Random;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.settings.Settings;

/**
 * Provides additional information to {@link InstancioServiceProvider}.
 *
 * @since 2.12.0
 */
@ExperimentalApi
public interface ServiceProviderContext {

    /**
     * Returns a read-only instance of the {@code Settings}.
     *
     * @return read-only instance of settings
     * @since 2.12.0
     */
    Settings getSettings();

    /**
     * Returns the random instance that should be used for generating
     * values to ensure reproducible results.
     *
     * @return the random instance
     * @since 2.13.0
     */
    Random random();
}
