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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

/**
 * Generator spec for {@link Duration}.
 *
 * @since 1.5.4
 */
public interface DurationGeneratorSpec extends GeneratorSpec<Duration> {

    /**
     * Generate a {@link Duration} in the given range, measured in specified units.
     *
     * @param minAmount minimum duration amount (inclusive)
     * @param maxAmount maximum duration amount (inclusive)
     * @param unit      unit the duration is measured in
     * @return spec builder
     * @since 1.5.4
     */
    DurationGeneratorSpec of(long minAmount, long maxAmount, TemporalUnit unit);

    /**
     * Allow a {@link Duration} of length zero to be generated.
     *
     * @return spec builder
     * @since 1.5.4
     */
    DurationGeneratorSpec allowZero();
}
