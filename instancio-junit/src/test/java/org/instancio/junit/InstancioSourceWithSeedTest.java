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

import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class InstancioSourceWithSeedTest {
    private static final int EXPECTED_INT = 12345;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, EXPECTED_INT)
            .set(Keys.INTEGER_MAX, EXPECTED_INT + 1);

    @Seed(1234)
    @InstancioSource(String.class)
    @ParameterizedTest
    void withSeed(final String value) {
        final String expected = "XTYQ";
        assertThat(value).isEqualTo(expected);
    }

    @InstancioSource(IntegerHolder.class)
    @ParameterizedTest
    void withSettings(final IntegerHolder holder) {
        assertThat(holder.getPrimitive()).isEqualTo(EXPECTED_INT);
        assertThat(holder.getWrapper()).isEqualTo(EXPECTED_INT);
    }
}
