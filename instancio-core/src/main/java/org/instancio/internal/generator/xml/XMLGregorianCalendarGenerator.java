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
package org.instancio.internal.generator.xml;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.time.LocalDateTimeGenerator;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.instancio.internal.util.ExceptionUtils.logException;

public class XMLGregorianCalendarGenerator extends AbstractGenerator<XMLGregorianCalendar> {

    private static final DatatypeFactory DATATYPE_FACTORY = getDatatypeFactory();

    private final Generator<LocalDateTime> localDateTimeGenerator;

    public XMLGregorianCalendarGenerator(final GeneratorContext context) {
        super(context);
        this.localDateTimeGenerator = new LocalDateTimeGenerator(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected XMLGregorianCalendar tryGenerateNonNull(final Random random) {
        if (DATATYPE_FACTORY == null) {
            return null;
        }

        final LocalDateTime localDateTime = localDateTimeGenerator.generate(random);

        return DATATYPE_FACTORY.newXMLGregorianCalendar(
                localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private static DatatypeFactory getDatatypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            logException("Error instantiating javax.xml.datatype.DatatypeFactory", ex);
            return null;
        }
    }
}
