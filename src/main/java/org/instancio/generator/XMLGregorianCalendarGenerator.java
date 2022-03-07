package org.instancio.generator;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLGregorianCalendarGenerator implements ValueGenerator<XMLGregorianCalendar> {

    @Override
    public XMLGregorianCalendar generate() {
        // TODO randomize
        LocalDateTime localDateTime = LocalDateTime.now();
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
