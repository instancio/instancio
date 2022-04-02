/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        assertGeneratedValues(byte.class, 2000, 60);
    }

    @Test
    void createShort() {
        assertGeneratedValues(short.class, 1000, 90);
    }

    @Test
    void createInt() {
        assertGeneratedValues(int.class, 1000, 97);
    }

    @Test
    void createLong() {
        assertGeneratedValues(long.class, 1000, 99);
    }

    @Test
    void createFloat() {
        assertGeneratedValues(float.class, 1000, 97);
    }

    @Test
    void createDouble() {
        assertGeneratedValues(double.class, 1000, 99);
    }

    private void assertGeneratedValues(final Class<?> klass, final int sampleSize, final int minExpectedResults) {
        for (int i = 0; i < sampleSize; i++) {
            results.add(Instancio.of(klass).create());
        }

        assertThat(results).hasSizeGreaterThanOrEqualTo(minExpectedResults);
    }
}
