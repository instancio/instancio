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
package org.external.errorhandling;

import org.instancio.Instancio;

import static org.instancio.Select.allStrings;

class EmitGeneratorNoItemsLeftToEmitErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.ofList(String.class)
                .size(3)
                .generate(allStrings(), gen -> gen.emit().items("one", "two").whenEmptyThrowException())
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.EmitGeneratorNoItemsLeftToEmitErrorMessageTest.methodUnderTest(EmitGeneratorNoItemsLeftToEmitErrorMessageTest.java:29)

                Reason: no item is available to emit() for node:

                class String (depth=1)

                 │ Path to root:
                 │   <1:String>
                 │    └──<0:List>   <-- Root
                 │
                 │ Format: <depth:class: field>

                Previously emitted values:

                 -> Node:   <1:String>
                    Values: [one, two]

                Another value is required for:

                 -> Node:   <1:String>

                But there are no values left to emit.
                Throwing exception because 'whenEmptyThrowException()' is enabled.

                """;
    }
}
