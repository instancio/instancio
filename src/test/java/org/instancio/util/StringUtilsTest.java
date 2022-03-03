package org.instancio.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilsTest {

    @Test
    void repeat() {
        assertThat(StringUtils.repeat("a", 3)).isEqualTo("aaa");
        assertThatThrownBy(() -> StringUtils.repeat("a", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
