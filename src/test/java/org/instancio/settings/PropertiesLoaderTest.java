package org.instancio.settings;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesLoaderTest {

    @Test
    void load() {
        final Properties props = new PropertiesLoader().load("instancio-test.properties");
        assertThat(props).isNotNull();
        assertThat(props.get(Setting.LONG_MAX.key())).isNotNull();
    }
}
