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
package org.instancio.internal.generator;

import org.instancio.generator.Hints;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorResultTest {

    @Test
    void isNull() {
        final GeneratorResult result = GeneratorResult.nullResult();
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.isIgnored()).isFalse();
    }

    @Test
    void isEmpty() {
        final GeneratorResult result = GeneratorResult.emptyResult();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isIgnored()).isFalse();
    }

    @Test
    void isIgnored() {
        final GeneratorResult result = GeneratorResult.ignoredResult();
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.isIgnored()).isTrue();
    }

    @Test
    void create() {
        final GeneratorResult result = GeneratorResult.create("foo", Hints.builder().build());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.isIgnored()).isFalse();
    }

    @Test
    void verifyToString() {
        assertThat(GeneratorResult.create("foo", Hints.builder().build()))
                .hasToString("Result[foo, Hints[afterGenerate=null, hints={}]]");

        assertThat(GeneratorResult.emptyResult()).hasToString("Result[EMPTY]");
        assertThat(GeneratorResult.nullResult()).hasToString("Result[NULL]");
        assertThat(GeneratorResult.delayed()).hasToString("Result[DELAYED]");
        assertThat(GeneratorResult.ignoredResult()).hasToString("Result[IGNORED]");
    }
}
