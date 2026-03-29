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
package org.instancio.protobuf.generator.specs;

import com.google.protobuf.Duration;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.GeneratorSpec;

/**
 * Spec for generating {@link Duration} values.
 *
 * <p>Bounds are expressed as {@link java.time.Duration} since protobuf
 * {@code Duration} is a signed measure of elapsed time (seconds + nanos).
 *
 * @since 6.0.0
 */
@ExperimentalApi
public interface ProtoDurationGeneratorSpec extends GeneratorSpec<Duration> {

    /**
     * Generate a non-negative {@link Duration} (from zero to the default max).
     *
     * @return spec builder
     * @since 6.0.0
     */
    ProtoDurationGeneratorSpec positive();

    /**
     * Generate a negative {@link Duration} (from the default min to zero).
     *
     * @return spec builder
     * @since 6.0.0
     */
    ProtoDurationGeneratorSpec negative();

    /**
     * Specifies the lower bound.
     *
     * @param min lower bound (inclusive)
     * @return spec builder
     * @since 6.0.0
     */
    ProtoDurationGeneratorSpec min(java.time.Duration min);

    /**
     * Specifies the upper bound.
     *
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 6.0.0
     */
    ProtoDurationGeneratorSpec max(java.time.Duration max);

    /**
     * Generate a value within the given range.
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 6.0.0
     */
    ProtoDurationGeneratorSpec range(java.time.Duration min, java.time.Duration max);
}
