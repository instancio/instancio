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
package org.instancio.generators.coretypes;

import org.instancio.internal.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.GeneratedHintsAssert.assertHints;

@SettingsTag
@NonDeterministicTag
class StringGeneratorTest {
    private static final int SAMPLE_SIZE = 1000;
    private static final Class<Object> ANY_CLASS = Object.class;
    private static final ModelContext<?> context = ModelContext.builder(ANY_CLASS)
            .withSettings(Settings.defaults()
                    .set(Setting.STRING_MIN_LENGTH, 1)
                    .set(Setting.STRING_MAX_LENGTH, 1)
                    .set(Setting.STRING_ALLOW_EMPTY, true)
                    .set(Setting.STRING_NULLABLE, true))
            .build();


    @Test
    void generate() {
        final StringGenerator generator = new StringGenerator(context);
        final Set<Object> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            results.add(generator.generate());
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
        final StringGenerator generator = new StringGenerator(context.toBuilder()
                .withSettings(Settings.create()
                        .set(Setting.STRING_NULLABLE, false)
                        .set(Setting.STRING_ALLOW_EMPTY, false))
                .build());

        // Override generator length
        generator.minLength(length).maxLength(length);

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final String result = generator.generate();
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
