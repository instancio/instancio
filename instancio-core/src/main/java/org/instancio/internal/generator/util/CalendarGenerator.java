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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.time.ZonedDateTimeGenerator;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarGenerator extends AbstractGenerator<Calendar> implements TemporalGeneratorSpec<Calendar> {

    private final ZonedDateTimeGenerator delegate;

    public CalendarGenerator(final GeneratorContext context) {
        super(context);
        this.delegate = new ZonedDateTimeGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "calendar()";
    }

    @Override
    public TemporalGeneratorSpec<Calendar> past() {
        delegate.past();
        return this;
    }

    @Override
    public TemporalGeneratorSpec<Calendar> future() {
        delegate.future();
        return this;
    }

    @Override
    public TemporalGeneratorSpec<Calendar> range(final Calendar start, final Calendar end) {
        delegate.range(
                ZonedDateTime.ofInstant(start.toInstant(), start.getTimeZone().toZoneId()),
                ZonedDateTime.ofInstant(end.toInstant(), end.getTimeZone().toZoneId()));
        return this;
    }

    @Override
    public Calendar generate(final Random random) {
        return GregorianCalendar.from(delegate.generate(random));
    }
}
