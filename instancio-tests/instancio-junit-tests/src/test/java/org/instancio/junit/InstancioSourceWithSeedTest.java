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
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.internal.ThreadLocalRandom;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(InstancioExtension.class)
class InstancioSourceWithSeedTest {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioSourceWithSeedTest.class);
    private static final String EXPECTED_STRING = "YDOQGZUVWOINKQNTDAONTOBMAYC";
    private static final int STRING_MIN_LENGTH = 20;
    private static final long SEED = -1234;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, STRING_MIN_LENGTH);

    @Seed(SEED)
    @InstancioSource(String.class)
    @ParameterizedTest
    @DisplayName("Parameterized test: generate value using seed")
    void parameterizedWithSeed(final String value) {
        LOG.debug("ThreadLocalRandom seed: {}", ThreadLocalRandom.getInstance().get().getSeed());
        assertThat(ThreadLocalRandom.getInstance().get().getSeed()).isEqualTo(SEED);
        assertThat(value).isEqualTo(EXPECTED_STRING);
    }

    @Seed(SEED)
    @RepeatedTest(10)
    @DisplayName("Non-parameterized test: generate value using seed")
    void nonParameterizedWithSeed() {
        LOG.debug("ThreadLocalRandom seed: {}", ThreadLocalRandom.getInstance().get().getSeed());
        assertThat(ThreadLocalRandom.getInstance().get().getSeed()).isEqualTo(SEED);
        assertThat(Instancio.create(String.class)).isEqualTo(EXPECTED_STRING);
    }

    @NonDeterministicTag("Assuming the random seed will not be equal to the SEED constant")
    @InstancioSource(StringHolder.class)
    @ParameterizedTest
    void withSettings(final StringHolder holder) {
        LOG.debug("ThreadLocalRandom seed: {}", ThreadLocalRandom.getInstance().get().getSeed());
        assertThat(ThreadLocalRandom.getInstance().get().getSeed()).isNotEqualTo(SEED);
        assertThat(holder.getValue()).hasSizeGreaterThanOrEqualTo(STRING_MIN_LENGTH);
    }
}
