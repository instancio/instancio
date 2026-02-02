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

import static org.instancio.Select.allInts;

class TypeMismatchWithArrayElementTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Integer[].class)
                .set(allInts(), "invalid arg")
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.TypeMismatchWithArrayElementTest.methodUnderTest(TypeMismatchWithArrayElementTest.java:28)

                Reason: array element type mismatch: class Integer[] (depth=0)

                 │ Path to root:
                 │   <0:Integer[]>   <-- Root
                 │
                 │ Format: <depth:class: field>


                Type mismatch:

                 -> Target type ..............: Integer
                 -> Provided argument type ...: String
                 -> Provided argument value ..: "invalid arg"

                """;
    }
}
