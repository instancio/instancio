package org.instancio.settings;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValueOfFunctionsTest {

    @Test
    void verifyFunctions() {
        assertThat(ValueOfFunctions.getFunction(Boolean.class).apply("true")).isEqualTo(true);
        assertThat(ValueOfFunctions.getFunction(Byte.class).apply("5")).isEqualTo((byte) 5);
        assertThat(ValueOfFunctions.getFunction(Short.class).apply("6")).isEqualTo((short) 6);
        assertThat(ValueOfFunctions.getFunction(Integer.class).apply("7")).isEqualTo(7);
        assertThat(ValueOfFunctions.getFunction(Long.class).apply("8")).isEqualTo(8L);
        assertThat(ValueOfFunctions.getFunction(Float.class).apply("10.8")).isEqualTo(10.8f);
        assertThat(ValueOfFunctions.getFunction(Double.class).apply("10.2")).isEqualTo(10.2d);
    }
}
