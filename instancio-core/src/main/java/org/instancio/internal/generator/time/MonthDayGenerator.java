/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.generator.specs.MonthDaySpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

import java.time.MonthDay;

public class MonthDayGenerator extends AbstractGenerator<MonthDay>
        implements MonthDaySpec {

    // a leap year, so that February 29 can be generated
    private static final int YEAR = 2000;

    private MonthDay min = MonthDay.of(1, 1);
    private MonthDay max = MonthDay.of(12, 31);

    private final LocalDateGenerator delegate;

    public MonthDayGenerator(final GeneratorContext context) {
        super(context);
        delegate = new LocalDateGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "monthDay()";
    }

    @Override
    public MonthDayGenerator min(final MonthDay min) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        return this;
    }

    @Override
    public MonthDayGenerator max(final MonthDay max) {
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public MonthDayGenerator range(final MonthDay min, final MonthDay max) {
        this.min = ApiValidator.notNull(min, "MonthDay start must not be null");
        this.max = ApiValidator.notNull(max, "MonthDay end must not be null");
        ApiValidator.isTrue(min.compareTo(max) <= 0,
                "start must not exceed end: %s, %s", this.min, this.max);
        return this;
    }

    @Override
    public MonthDayGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected MonthDay tryGenerateNonNull(final Random random) {
        delegate.range(min.atYear(YEAR), max.atYear(YEAR));
        return MonthDay.from(delegate.tryGenerateNonNull(random));
    }
}
