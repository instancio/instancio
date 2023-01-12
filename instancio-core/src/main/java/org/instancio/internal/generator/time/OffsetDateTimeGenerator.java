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

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class OffsetDateTimeGenerator extends JavaTimeTemporalGenerator<OffsetDateTime> {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private final LocalDateTimeGenerator delegate;

    public OffsetDateTimeGenerator(final GeneratorContext context) {
        super(context,
                OffsetDateTime.ofInstant(DEFAULT_MIN, ZONE_ID),
                OffsetDateTime.ofInstant(DEFAULT_MAX, ZONE_ID));

        delegate = new LocalDateTimeGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "offsetDateTime()";
    }

    @Override
    OffsetDateTime getLatestPast() {
        return OffsetDateTime.now().minusSeconds(1);
    }

    @Override
    OffsetDateTime getEarliestFuture() {
        return OffsetDateTime.now().plusMinutes(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public OffsetDateTime generate(final Random random) {
        delegate.range(min.toLocalDateTime(), max.toLocalDateTime());
        return OffsetDateTime.of(delegate.generate(random), ZoneOffset.UTC);
    }
}
