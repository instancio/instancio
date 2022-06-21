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
package org.instancio.test.properties;

import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GlobalSeedInstancioSourceTest {

    @ParameterizedTest
    @InstancioSource(value = {String.class, String.class})
    void parameterized(final String param1, final String param2) {
        assertThat(param1)
                .as("Distinct parameter values should be generated")
                .isNotEqualTo(param2);

        final Result<String> nonParam = Instancio.of(String.class).asResult();

        // (!) Random instance gets reset after parameters are generated,
        // therefore nonParam value is the same as param1
        assertThat(nonParam.get()).isEqualTo(param1);
        assertThat(nonParam.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);
    }
}
