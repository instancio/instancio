/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.junit.generate;

import org.instancio.junit.Generate;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GenerateParameterWithSettingsAnnotationSeedTest {

    private static final long SEED = -1;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SEED, SEED);

    private static final Set<Object> results = new HashSet<>();

    @RepeatedTest(10)
    void method1(
            @Generate final UUID generatedValue1,
            @Generate final UUID generatedValue2) {

        addAndAssertResults(generatedValue1, generatedValue2);
    }

    @ParameterizedTest
    @ValueSource(strings = "foo")
    void method2(
            final String string,
            @Generate final UUID generatedValue1,
            @Generate final UUID generatedValue2) {

        assertThat(string).isEqualTo("foo");
        addAndAssertResults(generatedValue1, generatedValue2);
    }

    private static void addAndAssertResults(final UUID generatedValue1, final UUID generatedValue2) {
        assertThat(generatedValue1).isNotNull();
        assertThat(generatedValue2).isNotNull();

        results.add(generatedValue1);
        results.add(generatedValue2);

        assertThat(results).hasSize(1);
    }
}
