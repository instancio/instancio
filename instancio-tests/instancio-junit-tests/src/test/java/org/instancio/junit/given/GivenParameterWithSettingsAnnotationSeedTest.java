/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.junit.given;

import org.instancio.junit.Given;
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
class GivenParameterWithSettingsAnnotationSeedTest {

    private static final long SEED = -1;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SEED, SEED);

    private static final Set<Object> results = new HashSet<>();

    @RepeatedTest(10)
    void method1(
            @Given final UUID param1,
            @Given final UUID param2) {

        addAndAssertResults(param1, param2);
    }

    @ParameterizedTest
    @ValueSource(strings = "foo")
    void method2(
            final String string,
            @Given final UUID param1,
            @Given final UUID param2) {

        assertThat(string).isEqualTo("foo");
        addAndAssertResults(param1, param2);
    }

    private static void addAndAssertResults(final UUID value1, final UUID value2) {
        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();

        results.add(value1);
        results.add(value2);

        assertThat(results).hasSize(2);
    }
}
