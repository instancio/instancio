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

import org.instancio.generator.hints.MapHint;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class MapHintTest {

    @Test
    void verifyToString() {
        assertThat(MapHint.builder().build())
                .hasToString("MapHint[generateEntries=0," +
                        " nullableMapKeys=false," +
                        " nullableMapValues=false," +
                        " withEntries={}]");

        assertThat(MapHint.builder()
                .generateEntries(3)
                .nullableMapKeys(true)
                .nullableMapValues(true)
                .withEntries(new HashMap<String, String>() {{
                    put("foo", "bar");
                }})
                .build())
                .hasToString("MapHint[generateEntries=3," +
                        " nullableMapKeys=true," +
                        " nullableMapValues=true," +
                        " withEntries={foo=bar}]");
    }
}
