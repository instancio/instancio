/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.lang;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.random.RandomProviderImpl;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.test.support.tags.SettingsTag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.GeneratedHintsAssert.assertHints;

@SettingsTag
@NonDeterministicTag
class StringGeneratorTest {
    private static final int SAMPLE_SIZE = 1000;
    private static final Settings settings = Settings.defaults()
            .set(Keys.STRING_MIN_LENGTH, 1)
            .set(Keys.STRING_MAX_LENGTH, 1)
            .set(Keys.STRING_ALLOW_EMPTY, true)
            .set(Keys.STRING_NULLABLE, true);

    private static final RandomProvider random = new RandomProviderImpl();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private final StringGenerator generator = new StringGenerator(context);

    @Test
    void generate() {
        final Set<Object> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            results.add(generator.generate(random));
        }

        assertThat(results)
                .as("26 letters + null + empty string")
                .hasSize(28)
                .containsNull()
                .containsOnlyOnce("")
                .contains(upperCaseLettersAtoZ());

        assertHints(generator.getHints())
                .nullableResult(true)
                .ignoreChildren(true);
    }

    @Test
    void generateOverrideSettings() {
        final int length = 10;
        final GeneratorContext ctxWithUpdatedSettings = new GeneratorContext(
                Settings.from(settings)
                        .set(Keys.STRING_NULLABLE, false)
                        .set(Keys.STRING_ALLOW_EMPTY, false),
                random);
        final StringGenerator generator = new StringGenerator(ctxWithUpdatedSettings);

        // Override generator length
        generator.length(length);

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final String result = generator.generate(random);
            assertThat(result).hasSize(length);
        }
    }

    private static String[] upperCaseLettersAtoZ() {
        String[] expected = new String[26];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = String.valueOf((char) ('A' + i));
        }
        return expected;
    }

    @Test
    void supports() {
        assertThat(generator.supports(String.class)).isTrue();
        assertThat(generator.supports(CharSequence.class)).isFalse();
    }
}
