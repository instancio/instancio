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
package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.GeneratorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLGregorianCalendarGenerator extends AbstractRandomGenerator<XMLGregorianCalendar> {
    private static final Logger LOG = LoggerFactory.getLogger(XMLGregorianCalendarGenerator.class);

    private final Generator<LocalDateTime> localDateTimeGenerator;

    public XMLGregorianCalendarGenerator(final GeneratorContext context) {
        super(context);
        this.localDateTimeGenerator = new LocalDateTimeGenerator(context);
    }

    @Override
    public XMLGregorianCalendar generate() {
        LocalDateTime localDateTime = localDateTimeGenerator.generate();
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        } catch (DatatypeConfigurationException ex) {
            LOG.debug("Error generating XMLGregorianCalendar; returning a null", ex);
            return null;
        }
    }
}
