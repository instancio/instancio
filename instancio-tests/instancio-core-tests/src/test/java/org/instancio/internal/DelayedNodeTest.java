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
package org.instancio.internal;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.testsupport.fixtures.Fixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DelayedNodeTest {

    @Test
    void verifyToString() {
        final DelayedNode delayedNode = new DelayedNode(Fixtures.node(String.class),
                GeneratorResult.create("foo", Hints.afterGenerate(AfterGenerate.APPLY_SELECTORS)));

        assertThat(delayedNode).hasToString(
                "DelayedNode[Node[String, depth=0, type=String]," +
                        " Result[foo, Hints[afterGenerate=APPLY_SELECTORS, hints={}]]]");

        assertThat(new DelayedNode(null, null))
                .hasToString("DelayedNode[null, null]");
    }
}
