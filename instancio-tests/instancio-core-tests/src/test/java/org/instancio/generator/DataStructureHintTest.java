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
package org.instancio.generator;

import org.instancio.generator.hints.DataStructureHint;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DataStructureHintTest {

    @Test
    void verifyToString() {
        assertThat(DataStructureHint.builder().build())
                .hasToString("DataStructureHint[dataStructureSize=0," +
                        " nullableElements=false, nullableMapKeys=false," +
                        " nullableMapValues=false, withElements=[]]");

        assertThat(DataStructureHint.builder()
                .dataStructureSize(3)
                .nullableElements(true)
                .nullableMapKeys(true)
                .nullableMapValues(true)
                .withElements(Arrays.asList("foo", "bar"))
                .build())
                .hasToString("DataStructureHint[dataStructureSize=3," +
                        " nullableElements=true, nullableMapKeys=true," +
                        " nullableMapValues=true, withElements=[foo, bar]]");
    }
}
