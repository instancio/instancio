/*
 * Copyright 2022-2024 the original author or authors.
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

import java.time.Instant;

/**
 * Spec for generating {@link Instant} values.
 *
 * @since 2.6.0
 */
public interface InstantSpec extends TemporalSpec<Instant> {

    @Override
    InstantSpec past();

    @Override
    InstantSpec future();

    @Override
    InstantSpec range(Instant start, Instant end);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    InstantSpec nullable();
}
