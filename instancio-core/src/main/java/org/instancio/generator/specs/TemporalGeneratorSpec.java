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
package org.instancio.generator.specs;

import org.instancio.documentation.NonDeterministic;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Generator spec for {@link java.time.temporal.Temporal} types.
 *
 * @param <T> temporal type
 * @since 1.1.2
 */
public interface TemporalGeneratorSpec<T extends @Nullable Object> extends
        AsGeneratorSpec<T>,
        NullableGeneratorSpec<T> {

    /**
     * Generate a value in the past.
     *
     * @return spec builder
     */
    @NonDeterministic
    TemporalGeneratorSpec<T> past();

    /**
     * Generate a value in the future.
     *
     * @return spec builder
     */
    @NonDeterministic
    TemporalGeneratorSpec<T> future();

    /**
     * Specifies the lower bound.
     *
     * @param min lower bound (inclusive)
     * @return spec builder
     * @see #range(Object, Object)
     * @since 4.6.0
     */
    TemporalGeneratorSpec<T> min(@NonNull T min);

    /**
     * Specifies the upper bound.
     *
     * @param max upper bound (inclusive)
     * @return spec builder
     * @see #range(Object, Object)
     * @since 4.6.0
     */
    TemporalGeneratorSpec<T> max(@NonNull T max);

    /**
     * Generate a value within the given range.
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     * @see #min(Object)
     * @see #max(Object)
     */
    TemporalGeneratorSpec<T> range(@NonNull T min, @NonNull T max);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    TemporalGeneratorSpec<T> nullable();
}
