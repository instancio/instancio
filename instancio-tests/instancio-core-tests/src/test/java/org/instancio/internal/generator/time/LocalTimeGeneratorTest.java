/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.generator.time;

import java.time.LocalTime;

class LocalTimeGeneratorTest extends TemporalGeneratorSpecTestTemplate<LocalTime> {

    private static final LocalTime START = LocalTime.of(1, 1, 2);

    private final LocalTimeGenerator generator = new LocalTimeGenerator(context);

    @Override
    JavaTimeTemporalGenerator<LocalTime> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "localTime()";
    }

    @Override
    LocalTime getNow() {
        return LocalTime.now();
    }

    @Override
    LocalTime getTemporalMin() {
        return LocalTime.MIN;
    }

    @Override
    LocalTime getTemporalMax() {
        return LocalTime.MAX;
    }

    @Override
    LocalTime getStart() {
        return START;
    }

    @Override
    LocalTime getStartMinusSmallestIncrement() {
        return START.minusNanos(1);
    }

    @Override
    LocalTime getStartPlusRandomSmallIncrement() {
        return START.plusNanos(random.intRange(1, 1000));
    }

    @Override
    LocalTime getStartPlusRandomLargeIncrement() {
        // (start + increment) should not cross over the 24-hour mark
        return START.plusHours(random.intRange(1, 22));
    }
}
