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
package org.instancio.generators;

import org.instancio.internal.ModelContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateTimeGenerator extends AbstractRandomGenerator<LocalDateTime> {

    public LocalDateTimeGenerator(final ModelContext<?> context) {
        super(context);
    }

    @Override
    public LocalDateTime generate() {
        final LocalDate date = LocalDate.ofYearDay(
                random().intBetween(1900, 2101),
                random().intBetween(1, 366));

        final LocalTime time = LocalTime.of(
                random().intBetween(0, 24),
                random().intBetween(0, 60),
                random().intBetween(0, 60),
                random().intBetween(0, 1_000_000_000));

        return LocalDateTime.of(date, time);
    }
}
