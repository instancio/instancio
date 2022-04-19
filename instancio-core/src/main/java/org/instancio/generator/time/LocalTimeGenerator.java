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

import java.time.LocalTime;

public class LocalTimeGenerator extends AbstractTemporalGenerator<LocalTime> {

    private static final LocalTime MIN = LocalTime.of(0, 0, 0);
    private static final LocalTime MAX = LocalTime.of(23, 59, 59);

    public LocalTimeGenerator(final GeneratorContext context) {
        super(context, MIN, MAX);
    }

    @Override
    LocalTime now() {
        return LocalTime.now();
    }

    @Override
    LocalTime getEarliestFuture() {
        return LocalTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.isBefore(max), "Start time must be before end time: %s, %s", min, max);
    }

    @Override
    public LocalTime generate(final RandomProvider random) {
        return LocalTime.ofNanoOfDay(random.longRange(
                min.toNanoOfDay(),
                max.toNanoOfDay()));
    }
}
