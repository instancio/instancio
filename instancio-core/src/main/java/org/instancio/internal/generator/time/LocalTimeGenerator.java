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
package org.instancio.internal.generator.time;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;

import java.time.LocalTime;

public class LocalTimeGenerator extends JavaTimeTemporalGenerator<LocalTime> {

    public LocalTimeGenerator(final GeneratorContext context) {
        super(context,
                LocalTime.of(0, 0, 0),
                LocalTime.of(23, 59, 59));
    }

    @Override
    public String apiMethod() {
        return "localTime()";
    }

    @Override
    LocalTime getLatestPast() {
        return LocalTime.now().minusSeconds(1);
    }

    @Override
    LocalTime getEarliestFuture() {
        return LocalTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public LocalTime generate(final Random random) {
        return LocalTime.ofNanoOfDay(random.longRange(
                min.toNanoOfDay(),
                max.toNanoOfDay()));
    }
}
