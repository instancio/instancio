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
package org.instancio;

import org.instancio.settings.Keys;

/**
 * Provides support for lenient mode.
 *
 * <p>Instancio supports two modes: strict and lenient, an idea inspired by
 * Mockito's highly useful strict stubbing feature.
 *
 * <p>In strict mode, unused selectors will trigger an error. In lenient mode,
 * unused selectors are ignored. By default, Instancio runs in strict mode.
 * Strict mode can be disabled by invoking the {@link #lenient()} method
 * or using the {@link Keys#MODE} setting.
 *
 * <p>Strict mode can catch potential data setup problems, therefore disabling
 * it is <b>not</b> recommended. A better alternative is to mark an individual
 * selector as lenient using the {@link LenientSelector#lenient()} method.
 *
 * @see Keys#MODE
 * @see LenientSelector
 * @since 4.0.0
 */
public interface LenientModeApi {

    /**
     * Disables strict mode in which unused selectors trigger an error.
     * In lenient mode unused selectors are simply ignored.
     *
     * <p>This method is a shorthand for:
     *
     * <pre>{@code
     * Example example = Instancio.of(Example.class)
     *     .withSetting(Keys.MODE, Mode.LENIENT)
     *     .create();
     * }</pre>
     *
     * @return API builder reference
     * @since 4.0.0
     */
    LenientModeApi lenient();
}
