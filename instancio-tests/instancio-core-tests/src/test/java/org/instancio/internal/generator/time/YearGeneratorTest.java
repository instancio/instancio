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

import java.time.Year;

class YearGeneratorTest extends TemporalGeneratorSpecTestTemplate<Year> {

    private static final Year START = Year.of(1970);

    private final YearGenerator generator = new YearGenerator(context);

    @Override
    JavaTimeTemporalGenerator<Year> getGenerator() {
        return generator;
    }

    @Override
    String getApiMethod() {
        return "year()";
    }

    @Override
    Year getNow() {
        return Year.now();
    }

    @Override
    Year getTemporalMin() {
        return Year.of(Year.MIN_VALUE);
    }

    @Override
    Year getTemporalMax() {
        return Year.of(Year.MAX_VALUE);
    }

    @Override
    Year getStart() {
        return START;
    }

    @Override
    Year getStartMinusSmallestIncrement() {
        return START.minusYears(1);
    }

    @Override
    Year getStartPlusRandomSmallIncrement() {
        return START.plusYears(random.intRange(1, 10));
    }

    @Override
    Year getStartPlusRandomLargeIncrement() {
        return START.plusYears(random.intRange(1, 10_000));
    }
}
