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

import org.instancio.Random;
import org.instancio.settings.Settings;

/**
 * A generator of values of a specific type.
 *
 * @param <T> type to generate
 * @since 1.0.1
 */
@FunctionalInterface
public interface Generator<T> extends GeneratorSpec<T> {

    /**
     * An optional method for generators that need to initialise state before
     * generating values. This method is guaranteed to be called:
     *
     * <ul>
     *   <li>before {@link #generate(Random)} is invoked</li>
     *   <li>exactly once per {@code Instancio.of()} invocation</li>
     * </ul>
     *
     * <p>If the same instance of a generator is shared across multiple
     * invocations of {@code Instancio.of()}, then this method will be called
     * exactly once per each invocation, resetting the generator's state.</p>
     *
     * <p>An instance of {@link Random} provided in the generator context can be
     * used if the initial state needs to be randomised. The context also contains
     * {@link Settings} which includes all settings overrides.</p>
     *
     * @param context generator context
     * @since 1.7.0
     */
    default void init(GeneratorContext context) {
        // no-op by default
    }

    /**
     * Returns a generated value.
     * <p>
     * If the generated value is random, it needs to be generated using the given
     * {@link Random} instance. This ensures the data is generated with
     * the same seed value and allows random data to be reproduced by specifying
     * the seed value.
     *
     * @param random provider for generating random values
     * @return generated value
     */
    T generate(Random random);

    /**
     * Hints provided by the generator to the engine.
     *
     * <p>The most important hint for custom generators is the {@link PopulateAction}.
     * This hint indicates whether the object created by this generator should
     * be populated further by the engine.</p>
     *
     * <p>For example, setting the hint to {@code PopulateAction.NULLS}
     * will cause Instancio to populate {@code null} fields on the object
     * returned by this generator:
     *
     * <pre>{@code
     *   @Override
     *   public Hints hints() {
     *       return Hints.withPopulateAction(PopulateAction.NULLS);
     *   }
     * }</pre>
     *
     * <p>If the action is not specified, default behaviour is
     * {@link PopulateAction#APPLY_SELECTORS}.</p>
     *
     * @return hints from this generator to the engine
     * @see Hint
     * @see PopulateAction
     * @since 1.7.0
     */
    default Hints hints() {
        return null;
    }
}
