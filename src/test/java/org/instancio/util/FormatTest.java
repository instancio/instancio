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
package org.instancio.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FormatTest {

    @Test
    void getTypeVariablesCsv() {
        assertThat(Format.getTypeVariablesCsv(Object.class)).isEmpty();
        assertThat(Format.getTypeVariablesCsv(List.class)).isEqualTo("E");
        assertThat(Format.getTypeVariablesCsv(Map.class)).isEqualTo("K, V");
    }

    @Test
    void paramsToCsv() {
        assertThat(Format.paramsToCsv(Collections.emptyList())).isEmpty();

        assertThat(Format.paramsToCsv(Collections.singletonList(String.class)))
                .isEqualTo("String.class");

        assertThat(Format.paramsToCsv(Arrays.asList(String.class, Integer.class)))
                .isEqualTo("String.class, Integer.class");
    }
}
