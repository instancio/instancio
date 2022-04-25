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
package org.instancio.test.features.stream;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@NonDeterministicTag("Assuming unique Strings are generated")
@FeatureTag(Feature.STREAM)
class CreateStreamTest {

    private static final int STRING_LENGTH = 50;
    private static final int EXPECTED_SIZE = 100;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, STRING_LENGTH)
            .set(Keys.STRING_MAX_LENGTH, STRING_LENGTH + 1)
            .lock();

    @Test
    void streamOfClass() {
        final Set<String> results = Instancio.stream(String.class).limit(EXPECTED_SIZE).collect(toSet());
        assertThat(results).hasSize(EXPECTED_SIZE);
    }

    @Test
    void streamOfTypeToken() {
        final Set<String> results = Instancio.stream(new TypeToken<String>() {}).limit(EXPECTED_SIZE).collect(toSet());
        assertThat(results).hasSize(EXPECTED_SIZE);
    }

    @Test
    void streamOfClassBuilderAPI() {
        final int overriddenLength = STRING_LENGTH * 2;
        final Set<String> results = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.string().length(overriddenLength))
                .stream()
                .limit(EXPECTED_SIZE).collect(toSet());

        assertThat(results)
                .hasSize(EXPECTED_SIZE)
                .allSatisfy(s -> assertThat(s.length()).isEqualTo(overriddenLength));
    }

    @Test
    void streamOfTypeTokenBuilderAPI() {
        final int overriddenLength = STRING_LENGTH * 2;
        final Set<String> results = Instancio.of(new TypeToken<String>() {})
                .generate(allStrings(), gen -> gen.string().length(overriddenLength))
                .stream()
                .limit(EXPECTED_SIZE).collect(toSet());

        assertThat(results)
                .hasSize(EXPECTED_SIZE)
                .allSatisfy(s -> assertThat(s.length()).isEqualTo(overriddenLength));
    }
}