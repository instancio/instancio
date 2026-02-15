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

import org.instancio.generator.GeneratorSpec;

import java.time.temporal.TemporalUnit;

/**
 * A spec for truncating temporal values.
 *
 * @param <T> temporal type
 * @since 4.2.0
 */
public interface TruncatableTemporalGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Truncates generated values to the specified unit.
     *
     * @param unit to truncate to
     * @return spec builder
     * @since 4.2.0
     */
    GeneratorSpec<T> truncatedTo(TemporalUnit unit);
}
