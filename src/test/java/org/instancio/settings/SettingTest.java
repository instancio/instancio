package org.instancio.settings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.settings.Setting.COLLECTION_NULLABLE;

class SettingTest {

    @Test
    void getByKey() {
        assertThat(Setting.getByKey(COLLECTION_NULLABLE.key())).isEqualTo(COLLECTION_NULLABLE);
    }

    @Test
    void getByInvalidKey() {
        assertThatThrownBy(() -> Setting.getByKey("foo"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Invalid key: 'foo'");
    }
}
