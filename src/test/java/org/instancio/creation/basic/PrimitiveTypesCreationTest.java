package org.instancio.creation.basic;

import org.instancio.Instancio;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Very basic and unscientific tests for randomly generated primitive types.
 */
@CreateTag
@NonDeterministicTag
class PrimitiveTypesCreationTest {

    private final Set<Object> results = new HashSet<>();

    @Test
    void createChar() {
        assertGeneratedValues(char.class, 2000, 26);
    }

    @Test
    void createBoolean() {
        assertGeneratedValues(boolean.class, 30, 2);
    }

    @Test
    void createByte() {
        assertGeneratedValues(byte.class, 100, 60);
    }

    @Test
    void createShort() {
        assertGeneratedValues(short.class, 100, 90);
    }

    @Test
    void createInt() {
        assertGeneratedValues(int.class, 100, 97);
    }

    @Test
    void createLong() {
        assertGeneratedValues(long.class, 100, 99);
    }

    @Test
    void createFloat() {
        assertGeneratedValues(float.class, 100, 97);
    }

    @Test
    void createDouble() {
        assertGeneratedValues(double.class, 100, 99);
    }

    private void assertGeneratedValues(final Class<?> klass, final int sampleSize, final int minExpectedResults) {
        for (int i = 0; i < sampleSize; i++) {
            results.add(Instancio.of(klass).create());
        }

        assertThat(results).hasSizeGreaterThanOrEqualTo(minExpectedResults);
    }
}
