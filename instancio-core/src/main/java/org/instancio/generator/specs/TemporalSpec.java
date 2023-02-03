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
package org.instancio.generator.specs;

import org.instancio.generator.ValueSpec;

import java.time.temporal.Temporal;

/**
 * Spec for generating {@link Temporal} types.
 *
 * @since 2.6.0
 */
public interface TemporalSpec<T> extends ValueSpec<T>, TemporalGeneratorSpec<T> {

    @Override
    TemporalSpec<T> past();

    @Override
    TemporalSpec<T> future();

    @Override
    TemporalSpec<T> range(T start, T end);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    TemporalSpec<T> nullable();
}
