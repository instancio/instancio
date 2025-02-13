/*
 * Copyright 2022-2025 the original author or authors.
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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.HintsAssert.assertHints;

class HintsTest {

    private static class FooHint implements Hint<FooHint> {
        @Override
        public String toString() {
            return "FooHint";
        }
    }

    @Test
    void getSet() {
        final FooHint one = new FooHint();
        final FooHint two = new FooHint();

        final Hints hints = Hints.builder()
                .with(one)
                .with(two)
                .build();

        assertThat(hints.get(FooHint.class))
                .isNotSameAs(one)
                .isSameAs(two);
    }

    @Test
    void emptyHintsShouldHaveNullAfterGenerate() {
        assertHints(Hints.builder().build())
                .afterGenerate(null);
    }

    @Test
    void verifyToString() {
        assertThat(Hints.builder().build())
                .hasToString("Hints[afterGenerate=null, hints={}]");

        assertThat(Hints.builder()
                .afterGenerate(AfterGenerate.POPULATE_ALL)
                .build())
                .hasToString("Hints[afterGenerate=POPULATE_ALL, hints={}]");

        assertThat(Hints.builder()
                .with(new FooHint())
                .build())
                .hasToString("Hints[afterGenerate=null," +
                        " hints={class org.instancio.generator.HintsTest$FooHint=FooHint}]");
    }
}
