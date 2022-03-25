package org.instancio.settings;

/**
 * A setting key for modifying configuration.
 * <p>
 * Configuration file is read from {@code instancio.properties}
 * at the root of the classpath.
 */
public interface SettingKey {

    /**
     * A key used in the properties file.
     *
     * @return property key
     */
    String key();

    /**
     * Type of the property value.
     *
     * @return value class
     */
    Class<?> type();

    /**
     * Default value for this key.
     *
     * @param <T> type of the value
     * @return default value
     */
    <T> T defaultValue();

}
