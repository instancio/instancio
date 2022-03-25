package org.instancio.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

    public Properties load(final String file) {
        final Properties properties = new Properties();

        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream(file)) {
            properties.load(inStream);
            return properties;
        } catch (IOException ex) {
            LOG.debug("Failed loading {}", file);
        }
        return properties;
    }
}
