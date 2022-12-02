/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.OnCompleteCallback;

/**
 * An action to be performed by the engine after an object
 * has been created by a {@link org.instancio.Generator}.
 *
 * @see Hints
 * @since 1.7.0
 */
public enum PopulateAction {

    /**
     * Indicates that an object created by a generator should not be modified
     * by the engine. This is true even if there are other suppliers or
     * generators with matching selectors present. Therefore, the engine will
     * <b>not</b> apply the following methods on the generated object:
     *
     * <ul>
     *   <li>{@code set()}</li>
     *   <li>{@code supply()}</li>
     *   <li>{@code generate()}</li>
     * </ul>
     *
     * <p><b>Note:</b> {@link OnCompleteCallback callbacks} provided via the
     * {@code onComplete()} method <b>will</b> still be executed.</p>
     *
     * @since 1.7.0
     */
    NONE,

    /**
     * Indicates that an object created by a generator should not be modified
     * by the engine unless there other suppliers or generators with matching
     * selectors present. This action allows customising objects created by
     * custom generators using {@code supply()} and {@code generate()} methods.
     *
     * @since 1.7.0
     */
    APPLY_SELECTORS,

    /**
     * Indicates that only {@code null} fields should be populated.
     * Primitive and non-null fields will not be populated unless
     * there are matching {@code supply} or {@code generate()} selectors present.
     *
     * @since 1.7.0
     */
    NULLS,

    /**
     * Indicates that only {@code null} fields and primitive fields
     * with default values should be populated. Default values for
     * primitive fields are:
     *
     * <ul>
     *   <li>Zero for all numeric types</li>
     *   <li>{@code false} for {@code boolean}</li>
     *   <li>{@code '\u0000'} null character for {@code char}</li>
     * </ul>
     *
     * @since 1.7.0
     */
    NULLS_AND_DEFAULT_PRIMITIVES,

    /**
     * A hint to populate all fields, whether {@code null} or not.
     * If a field had a previously assigned value, the value will
     * be overwritten with a new value.
     *
     * @since 1.7.0
     */
    ALL
}
