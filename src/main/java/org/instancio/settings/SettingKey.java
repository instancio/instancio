package org.instancio.settings;

public interface SettingKey {

    String key();

    Class<?> type();

    <T> T defaultValue();

}
