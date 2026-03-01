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

import org.instancio.generator.ValueSpec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Spec for generating {@link Number} types.
 *
 * @since 2.6.0
 */
public interface NumberSpec<T extends @Nullable Number> extends ValueSpec<T>, NumberGeneratorSpec<T> {

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     *
     */
    @Override
    NumberSpec<T> min(@NonNull T min);

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    NumberSpec<T> max(@NonNull T max);

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    NumberSpec<T> range(@NonNull T min, @NonNull T max);

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    NumberSpec<T> nullable();
}
