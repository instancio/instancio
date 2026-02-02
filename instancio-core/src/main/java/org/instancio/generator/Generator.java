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
package org.instancio.generator;

import org.instancio.Random;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.generator.hints.MapHint;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

/**
 * A class for generating values of a specific type.
 *
 * @param <T> type to generate
 * @since 1.0.1
 */
@FunctionalInterface
public interface Generator<T> extends GeneratorSpec<T> {

    /**
     * An optional method for generators that need to initialise state before
     * generating values.
     *
     * <p><b>Note:</b> this method is guaranteed to be called <b>before</b>
     * {@link #generate(Random)}, however, there is no guarantee as to
     * the number of times it will be invoked. It is possible for {@code init()}
     * to be called multiple times for a given generator instance.
     *
     * @param context generator context
     * @since 2.13.0
     */
    @ExperimentalApi
    default void init(GeneratorContext context) {
    }

    /**
     * Returns a generated value.
     * <p>
     * If this method produces random data, the data needs to be generated
     * using the provided {@link Random} instance. This ensures generated
     * values are reproducible for a given seed value.
     *
     * @param random provider for generating random values
     * @return generated value or {@code null} if value is nullable,
     * could not be generated, or generation is delegated to the engine
     */
    T generate(Random random);

    /**
     * Hints provided by the generator to the engine.
     *
     * <p>The most important hint for custom generators is {@link AfterGenerate}.
     * This hint indicates whether the object created by this generator:
     *
     * <ul>
     *   <li>should be populated (for example, if it has {@code null} fields)</li>
     *   <li>can be modified using selectors</li>
     * </ul>
     *
     * <p>For example, setting the hint to {@link AfterGenerate#POPULATE_NULLS}
     * will cause Instancio to populate {@code null} fields on the object
     * returned by this generator:
     *
     * <pre>{@code
     *   @Override
     *   public Hints hints() {
     *       return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
     *   }
     * }</pre>
     *
     * <p>If the action is not specified, default behaviour will be based on
     * the {@link AfterGenerate} value configured in the {@link Settings}
     * using the key {@link Keys#AFTER_GENERATE_HINT}.</p>
     * <p>
     * In addition, the following hints can be provided for populating
     * data structures:
     *
     * <ul>
     *   <li>{@link ArrayHint}</li>
     *   <li>{@link CollectionHint}</li>
     *   <li>{@link MapHint}</li>
     * </ul>
     *
     * @return hints from this generator to the engine
     * @see Hint
     * @see Hints
     * @see AfterGenerate
     * @since 2.0.0
     */
    default Hints hints() {
        return null;
    }
}
