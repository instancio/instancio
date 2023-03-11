/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.generator;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InternalGeneratorHintTest {

    @Test
    void verifyToString() {
        assertThat(InternalGeneratorHint.builder().build())
                .hasToString("GeneratorHint[targetClass=null, isDelegating=false, " +
                        "nullableResult=false, emitNull=false, excludeFromCallbacks=false]");

        assertThat(InternalGeneratorHint.builder()
                .targetClass(Map.class)
                .delegating(true)
                .nullableResult(true)
                .emitNull(true)
                .excludeFromCallbacks(true)
                .build())
                .hasToString("GeneratorHint[targetClass=java.util.Map, isDelegating=true, " +
                        "nullableResult=true, emitNull=true, excludeFromCallbacks=true]");
    }
}
