/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio;

import org.instancio.settings.Keys;

/**
 * Provides support for lenient mode.
 *
 * @param <T> the type of object to create
 * @see Keys#MODE
 * @since 4.0.0
 */
interface LenientMode<T> {

    /**
     * Disables strict mode in which unused selectors trigger an error.
     * In lenient mode unused selectors are simply ignored.
     *
     * <p>This method is a shorthand for:
     *
     * <pre>{@code
     * Example example = Instancio.of(Example.class)
     *     .withSettings(Settings.create().set(Keys.MODE, Mode.LENIENT))
     *     .create();
     * }</pre>
     *
     * @return API builder reference
     * @since 4.0.0
     */
    LenientMode<T> lenient();
}
