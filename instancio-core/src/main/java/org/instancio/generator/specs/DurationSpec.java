/*
 * Copyright 2022-2025 the original author or authors.
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

import java.time.Duration;
import java.time.temporal.TemporalUnit;

/**
 * Spec for generating {@link Duration}.
 *
 * @since 2.9.0
 */
public interface DurationSpec extends ValueSpec<Duration>, DurationGeneratorSpec {

    @Override
    DurationSpec min(long amount, TemporalUnit unit);

    @Override
    DurationSpec max(long amount, TemporalUnit unit);

    @Override
    DurationSpec of(long minAmount, long maxAmount, TemporalUnit unit);

    @Override
    DurationSpec allowZero();

    @Override
    DurationSpec nullable();
}
