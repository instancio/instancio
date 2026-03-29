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

import com.google.protobuf.Timestamp;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.documentation.NonDeterministic;
import org.instancio.generator.GeneratorSpec;

import java.time.Instant;

/**
 * Spec for generating {@link Timestamp} values.
 *
 * <p>Bounds are expressed as {@link Instant} since protobuf {@code Timestamp}
 * is a representation of a point in time (seconds + nanos since Unix epoch).
 *
 * @since 6.0.0
 */
@ExperimentalApi
public interface ProtoTimestampGeneratorSpec extends GeneratorSpec<Timestamp> {

    /**
     * Generate a {@link Timestamp} in the past.
     *
     * @return spec builder
     * @since 6.0.0
     */
    @NonDeterministic
    ProtoTimestampGeneratorSpec past();

    /**
     * Generate a {@link Timestamp} in the future.
     *
     * @return spec builder
     * @since 6.0.0
     */
    @NonDeterministic
    ProtoTimestampGeneratorSpec future();

    /**
     * Specifies the lower bound.
     *
     * @param min lower bound (inclusive)
     * @return spec builder
     * @since 6.0.0
     */
    ProtoTimestampGeneratorSpec min(Instant min);

    /**
     * Specifies the upper bound.
     *
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 6.0.0
     */
    ProtoTimestampGeneratorSpec max(Instant max);

    /**
     * Generate a value within the given range.
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 6.0.0
     */
    ProtoTimestampGeneratorSpec range(Instant min, Instant max);
}
