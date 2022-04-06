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
import org.instancio.internal.ThreadLocalRandomProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class InstancioExtensionWithoutSeedAnnotationTest {

    @Test
    @DisplayName("Same seed should be used within the test method")
    void sameSeedValuePerTestMethod() {
        assertThat(ThreadLocalRandomProvider.getInstance().get().getSeed())
                .isEqualTo(ThreadLocalRandomProvider.getInstance().get().getSeed());
    }

    @Test
    @DisplayName("Repeated calls to create() should produce distinct values")
    void eachCreateInstanceGeneratesDistinctValue() {
        final String result1 = Instancio.create(String.class);
        final String result2 = Instancio.create(String.class);
        assertThat(result1).isNotEqualTo(result2);
    }
}
