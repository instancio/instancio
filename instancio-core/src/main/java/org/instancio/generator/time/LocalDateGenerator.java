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

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.random.RandomProvider;

import java.time.LocalDate;

import static java.time.temporal.ChronoField.EPOCH_DAY;

public class LocalDateGenerator extends AbstractTemporalGenerator<LocalDate> {

    public LocalDateGenerator(final GeneratorContext context) {
        super(context,
                LocalDate.of(1970, 1, 1),
                LocalDate.now().plusYears(50));
    }

    @Override
    LocalDate now() {
        return LocalDate.now();
    }

    @Override
    LocalDate getEarliestFuture() {
        return LocalDate.now().plusDays(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.isBefore(max),
                "Start date must be before end date by at least 1 day: %s, %s", min, max);
    }

    @Override
    public LocalDate generate(final RandomProvider random) {
        return LocalDate.ofEpochDay(random.longRange(
                min.getLong(EPOCH_DAY),
                max.getLong(EPOCH_DAY)));
    }
}
