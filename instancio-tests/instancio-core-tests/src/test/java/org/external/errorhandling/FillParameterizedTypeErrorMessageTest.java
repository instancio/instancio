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
package org.external.errorhandling;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.generics.basic.Item;

class FillParameterizedTypeErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        final Item<String> object = new Item<>();
        Instancio.fill(object);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FillParameterizedTypeErrorMessageTest.methodUnderTest(FillParameterizedTypeErrorMessageTest.java:26)

                Reason: cannot fill() parameterized types

                The following methods do not support populating parameterized types, except List<E> and Map<K, V>:

                 -> Instancio.fill(Object)
                 -> Instancio.ofObject(Object).fill()

                The provided argument is of type:

                 -> Item<K>

                """;
    }
}
