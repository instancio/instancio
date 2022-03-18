package org.instancio.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLGregorianCalendarGenerator implements Generator<XMLGregorianCalendar> {
    private static final Logger LOG = LoggerFactory.getLogger(XMLGregorianCalendarGenerator.class);

    @Override
    public XMLGregorianCalendar generate() {
        // TODO randomize
        LocalDateTime localDateTime = LocalDateTime.now();
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        } catch (DatatypeConfigurationException ex) {
            LOG.debug("Error generating XMLGregorianCalendar; returning a null", ex);
            return null;
        }
    }
}
