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
package org.external.errorhandling;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.misc.StringFields;

import static org.instancio.Select.allStrings;

class EmitGeneratorNoItemsLeftToEmitWithDifferentFieldsErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringFields.class)
                .generate(allStrings(), gen -> gen.emit().items("one", "two", "three").whenEmptyThrowException())
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.EmitGeneratorNoItemsLeftToEmitWithDifferentFieldsErrorMessageTest.methodUnderTest(EmitGeneratorNoItemsLeftToEmitWithDifferentFieldsErrorMessageTest.java:29)

                Reason: no item is available to emit() for node:

                field StringFields.four (depth=1)

                 │ Path to root:
                 │   <1:StringFields: String four>
                 │    └──<0:StringFields>   <-- Root
                 │
                 │ Format: <depth:class: field>

                Previously emitted values:

                 -> Node:   <1:StringFields: String one>
                    Values: [one]

                 -> Node:   <1:StringFields: String two>
                    Values: [two]

                 -> Node:   <1:StringFields: String three>
                    Values: [three]

                Another value is required for:

                 -> Node:   <1:StringFields: String four>

                But there are no values left to emit.
                Throwing exception because 'whenEmptyThrowException()' is enabled.

                """;
    }
}
