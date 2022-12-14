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

import org.instancio.InstancioApi;
import org.instancio.OnCompleteCallback;
import org.instancio.TargetSelector;

import java.util.function.Function;

/**
 * An action hint that is passed from a generator to the engine
 * via the {@link Generator#hints()} method.
 * <p>
 * This hint indicates to the engine what action, if any, should
 * be applied to the object(s) created by the generator.
 *
 * @see Hints
 * @since 2.0.0
 */
public enum AfterGenerate {

    /**
     * Indicates that an object created by the generator should not be modified.
     * The engine will treat the object as read-only and assign it to the target field as is.
     * <p>
     * The following methods with matching selectors will not be applied to the object:
     *
     * <ul>
     *   <li>{@link InstancioApi#set(TargetSelector, Object)}</li>
     *   <li>{@link InstancioApi#supply(TargetSelector, Generator)} </li>
     *   <li>{@link InstancioApi#generate(TargetSelector, Function)} </li>
     * </ul>
     *
     * <p><b>Note:</b> {@link OnCompleteCallback callbacks} provided via the
     * {@code onComplete()} method <b>will</b> still be executed.</p>
     *
     * @since 2.0.0
     */
    DO_NOT_MODIFY,

    /**
     * Indicates that an object created by the generator can be modified using
     * any of the following methods:
     *
     * <ul>
     *   <li>{@link InstancioApi#set(TargetSelector, Object)}</li>
     *   <li>{@link InstancioApi#supply(TargetSelector, Generator)} </li>
     *   <li>{@link InstancioApi#generate(TargetSelector, Function)} </li>
     * </ul>
     *
     * @since 2.0.0
     */
    APPLY_SELECTORS,

    /**
     * Indicates that {@code null} fields declared by an object that was
     * created by the generator should be populated by the engine. In addition,
     * the object can be also be modified as described by {@link #APPLY_SELECTORS}.
     *
     * @since 2.0.0
     */
    POPULATE_NULLS,

    /**
     * Indicates that primitive fields with default values declared by an object
     * that was created by the generator should be populated by the engine.
     * In addition, the behaviour described by {@link #POPULATE_NULLS} applies as well.
     * <p>
     * Default values for primitive fields are defined as:
     *
     * <ul>
     *   <li>Zero for all numeric types</li>
     *   <li>{@code false} for {@code boolean}</li>
     *   <li>{@code '\u0000'} null character for {@code char}</li>
     * </ul>
     * <p>
     * For instance, if a {@code boolean} field was initialised to {@code true},
     * it will not be "populated". However, if it is {@code false}, then
     * it will be randomised to either {@code true} or {@code false}.
     *
     * @since 2.0.0
     */
    POPULATE_NULLS_AND_DEFAULT_PRIMITIVES,

    /**
     * A hint to populate all fields, regardless of their initial values.
     * This action will cause all the values to be overwritten with random data.
     * <p>
     * This is the default mode of internal generators.
     *
     * @since 2.0.0
     */
    POPULATE_ALL
}
