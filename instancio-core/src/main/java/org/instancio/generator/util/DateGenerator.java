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
package org.instancio.generator.util;

import org.instancio.Generator;
import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.time.LocalDateTimeGenerator;
import org.instancio.internal.random.RandomProvider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateGenerator extends AbstractGenerator<Date> {

    private final Generator<LocalDateTime> localDateTimeGenerator;

    public DateGenerator(final GeneratorContext context) {
        super(context);
        this.localDateTimeGenerator = new LocalDateTimeGenerator(context);
    }

    @Override
    public Date generate(final RandomProvider random) {
        final LocalDateTime ldt = localDateTimeGenerator.generate(random);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
