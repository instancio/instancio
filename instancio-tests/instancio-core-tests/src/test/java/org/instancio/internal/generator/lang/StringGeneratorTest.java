/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator.lang;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class StringGeneratorTest extends AbstractGeneratorTestTemplate<String, StringGenerator> {
    private static final int SAMPLE_SIZE = 1000;

    private final Settings settings = Settings.defaults()
            .set(Keys.STRING_MIN_LENGTH, 1)
            .set(Keys.STRING_MAX_LENGTH, 1)
            .set(Keys.STRING_ALLOW_EMPTY, true)
            .set(Keys.STRING_NULLABLE, true)
            .lock();

    private final StringGenerator generator = new StringGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "string()";
    }

    @Override
    protected StringGenerator generator() {
        return generator;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

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
}
