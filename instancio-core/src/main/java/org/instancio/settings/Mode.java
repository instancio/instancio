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
package org.instancio.settings;

import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;

/**
 * The mode is used to specify strictness level, either {@link #STRICT}
 * or {@link #LENIENT}, an idea borrowed from the Mockito library.
 *
 * <p>In strict mode, which is enabled by default, unused selectors will trigger
 * the {@link UnusedSelectorException}. The error notifies the user of which
 * selectors are redundant, with the goal of keeping tests clean and concise.</p>
 *
 * <p>Strict mode can be disabled as follows:
 *
 * <ul>
 *   <li>per object, {@link InstancioApi#lenient()} method</li>
 *   <li>via {@link Settings}, by setting {@link Keys#MODE} to {@link #LENIENT}</li>
 *   <li>or globally using {@code instancio.properties}</li>
 * </ul>
 *
 * @see InstancioApi#lenient()
 * @see Settings
 * @see Keys#MODE
 * @since 1.3.3
 */
public enum Mode {

    /**
     * Triggers an exception if at least one selector was not used
     * during object construction.
     * <p>
     * This mode is enabled by default.
     *
     * @since 1.3.3
     */
    STRICT,

    /**
     * In lenient mode, unused selectors are ignored and no exception is triggered.
     *
     * @since 1.3.3
     */
    LENIENT
}
