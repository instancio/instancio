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
        assertThat(result.isUnresolved()).isFalse();
        assertThat(result.isIgnored()).isFalse();
    }

    @Test
    void isUnresolved() {
        final GeneratorResult result = GeneratorResult.unresolvedResult();
        assertThat(result.isUnresolved()).isTrue();
        assertThat(result.isIgnored()).isFalse();
    }

    @Test
    void isIgnored() {
        final GeneratorResult result = GeneratorResult.ignoredResult();
        assertThat(result.isUnresolved()).isFalse();
        assertThat(result.isIgnored()).isTrue();
    }

    @Test
    void resolved() {
        final GeneratorResult result = GeneratorResult.resolved("foo", Hints.builder().build());
        assertThat(result.isUnresolved()).isFalse();
        assertThat(result.isIgnored()).isFalse();
    }

    @Test
    void verifyToString() {
        assertThat(GeneratorResult.resolved("foo", Hints.builder().build()))
                .hasToString("Result[foo, Hints[afterGenerate=null, hints={}]]");

        assertThat(GeneratorResult.unresolvedResult()).hasToString("Result[UNRESOLVED]");
        assertThat(GeneratorResult.nullResult()).hasToString("Result[NULL]");
        assertThat(GeneratorResult.delayedResult()).hasToString("Result[DELAYED]");
        assertThat(GeneratorResult.ignoredResult()).hasToString("Result[IGNORED]");
    }
}
