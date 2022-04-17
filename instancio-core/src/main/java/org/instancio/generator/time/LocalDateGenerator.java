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

import java.time.LocalDate;

import static java.time.temporal.ChronoField.EPOCH_DAY;

public class LocalDateGenerator extends AbstractGenerator<LocalDate> implements TemporalGeneratorSpec<LocalDate> {

    private static final LocalDate PAST = LocalDate.of(1970, 1, 1);
    private static final LocalDate FUTURE = LocalDate.now().plusYears(50);

    private LocalDate min = PAST;
    private LocalDate max = FUTURE;

    public LocalDateGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public TemporalGeneratorSpec<LocalDate> past() {
        min = PAST;
        max = LocalDate.now();
        return this;
    }

    @Override
    public TemporalGeneratorSpec<LocalDate> future() {
        min = LocalDate.now().plusDays(1);
        max = FUTURE;
        return this;
    }

    @Override
    public TemporalGeneratorSpec<LocalDate> range(final LocalDate startInclusive, final LocalDate endExclusive) {
        min = Verify.notNull(startInclusive, "Start date must not be null");
        max = Verify.notNull(endExclusive, "End date must not be null");
        Verify.isTrue(startInclusive.isBefore(endExclusive),
                "Start date must be before end date: %s, %s", startInclusive, endExclusive);
        return this;
    }

    @Override
    public LocalDate generate(final RandomProvider random) {
        return LocalDate.ofEpochDay(random.longRange(
                min.getLong(EPOCH_DAY),
                max.getLong(EPOCH_DAY)));
    }
}
