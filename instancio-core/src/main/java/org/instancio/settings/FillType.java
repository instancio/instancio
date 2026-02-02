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

import org.instancio.InstancioObjectApi;
import org.instancio.documentation.ExperimentalApi;

/**
 * Defines fill types for populating fields in existing objects when using
 * {@link InstancioObjectApi#fill()}. Each fill type specifies which fields
 * should be considered for population based on their current values.
 *
 * <p>Fill types follow a hierarchical structure where each subsequent type
 * builds upon the behavior of the previous types, adding additional capabilities.
 *
 * @see Keys#FILL_TYPE
 * @since 5.3.0
 */
@ExperimentalApi
public enum FillType {

    /**
     * Populates an object based solely on specified selectors.
     * Only the fields explicitly targeted by selectors are modified,
     * while all other fields remain unchanged.
     *
     * @since 5.3.0
     */
    APPLY_SELECTORS,

    /**
     * Populates all {@code null} fields with random values and applies
     * the specified selectors. This includes the behaviour of
     * {@link #APPLY_SELECTORS} and adds population of {@code null} fields.
     *
     * <p>Non-null fields remain unchanged unless explicitly targeted by selectors.
     *
     * @since 5.3.0
     */
    POPULATE_NULLS,

    /**
     * Populates both {@code null} fields and primitive fields containing
     * default values. This extends the behavior of {@link #POPULATE_NULLS}
     * by also addressing default primitive values.
     *
     * <p>A primitive field is considered to have a default value if it contains:
     * <ul>
     *   <li>{@code 0} for numeric types ({@code byte}, {@code short}, {@code int},
     *       {@code long}, {@code float}, {@code double})</li>
     *   <li>{@code false} for {@code boolean}</li>
     *   <li>{@code '\u0000'} (null character) for {@code char}</li>
     * </ul>
     *
     * <p>For example:
     * <ul>
     *   <li>A {@code boolean} field with the value {@code false} may be randomized.</li>
     *   <li>A {@code boolean} field with the value {@code true} will remain unchanged.</li>
     *   <li>An {@code int} field with the value {@code 0} may be randomized.</li>
     *   <li>An {@code int} field with any non-zero value will remain unchanged.</li>
     * </ul>
     *
     * @since 5.3.0
     */
    POPULATE_NULLS_AND_DEFAULT_PRIMITIVES
}
