/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator.domain.id.pol;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.time.LocalDateGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;

class PeselDateGenerator extends AbstractGenerator<String> {

    private static final LocalDate MIN = LocalDate.of(1800, 1, 1);
    private static final LocalDate MAX = LocalDate.of(2300, 1, 1).minusDays(1);

    private static final String MONTH_LITERAL = "MM";
    private static final DateTimeFormatter PESEL_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uu")
            .appendLiteral(MONTH_LITERAL)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter();

    private Generator<LocalDate> localDateGenerator;

    PeselDateGenerator(final GeneratorContext context) {
        super(context);
        localDateGenerator = new LocalDateGenerator(context).range(MIN, MAX);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final LocalDate localDate = localDateGenerator.generate(random);
        ApiValidator.notNull(localDate, "generated PESEL date must not be null");
        return localDate.format(PESEL_DATE_FORMATTER)
                .replace(MONTH_LITERAL, codedMonthFromDate(localDate));
    }

    PeselDateGenerator withLocalDate(final Generator<LocalDate> localDateGenerator) {
        if (localDateGenerator != null) {
            this.localDateGenerator = localDateGenerator;
        }
        return this;
    }

    private static String codedMonthFromDate(LocalDate localDate) {
        final int century = localDate.getYear() / 100;
        int codedMonth = localDate.getMonthValue();
        switch (century) {
            case 18:
                codedMonth += 80;
                break;
            case 20:
                codedMonth += 20;
                break;
            case 21:
                codedMonth += 40;
                break;
            case 22:
                codedMonth += 60;
                break;
            default:
                break;
        }
        return String.format("%02d", codedMonth);
    }
}
