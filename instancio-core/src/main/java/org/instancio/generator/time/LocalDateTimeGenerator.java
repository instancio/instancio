/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.time;

import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.time.Instant;
import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

public class LocalDateTimeGenerator extends AbstractGenerator<LocalDateTime> implements TemporalGeneratorSpec<LocalDateTime> {

    private static final LocalDateTime PAST = LocalDateTime.of(1970, 1, 1, 0, 0);
    private static final LocalDateTime FUTURE = LocalDateTime.now().plusYears(50);

    private LocalDateTime min = PAST;
    private LocalDateTime max = FUTURE;

    public LocalDateTimeGenerator(final GeneratorContext context) {
        super(context);
    }


    @Override
    public TemporalGeneratorSpec<LocalDateTime> past() {
        min = PAST;
        max = LocalDateTime.now();
        return this;
    }

    @Override
    public TemporalGeneratorSpec<LocalDateTime> future() {
        min = LocalDateTime.now().plusMinutes(1);
        max = FUTURE;
        return this;
    }

    @Override
    public TemporalGeneratorSpec<LocalDateTime> range(final LocalDateTime startInclusive, final LocalDateTime endExclusive) {
        min = Verify.notNull(startInclusive, "Start date must not be null");
        max = Verify.notNull(endExclusive, "End date must not be null");
        Verify.isTrue(startInclusive.isBefore(endExclusive),
                "Start date must be before end date: %s, %s", startInclusive, endExclusive);
        return this;
    }

    @Override
    public LocalDateTime generate(final RandomProvider random) {
        final Instant instant = Instant.ofEpochMilli(random.longBetween(
                min.toInstant(UTC).toEpochMilli(),
                max.toInstant(UTC).toEpochMilli()));

        return LocalDateTime.ofInstant(instant, UTC);
    }
}
