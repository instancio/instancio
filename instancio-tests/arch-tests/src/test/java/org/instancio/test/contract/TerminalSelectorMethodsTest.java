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
package org.instancio.test.contract;

import org.instancio.FieldSelectorBuilder;
import org.instancio.GroupableSelector;
import org.instancio.TypeSelectorBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.instancio.test.support.asserts.ClassAssert.assertThatClass;

class TerminalSelectorMethodsTest {

    /**
     * {@code atDepth()} should be a terminal method.
     */
    @ValueSource(classes = {
            FieldSelectorBuilder.class,
            TypeSelectorBuilder.class
    })
    @ParameterizedTest
    void atDepth(final Class<?> klass) {
        assertThatClass(klass)
                .withMethodNameMatching("atDepth")
                .hasSize(2)
                .haveReturnType(GroupableSelector.class);
    }

}
