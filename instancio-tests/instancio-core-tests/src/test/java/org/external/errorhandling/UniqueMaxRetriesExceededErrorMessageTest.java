/*
 * Copyright 2022-2024 the original author or authors.
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

import static org.instancio.Select.allBooleans;

class UniqueMaxRetriesExceededErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.ofList(Boolean.class)
                .size(10)
                .withUnique(allBooleans())
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.UniqueMaxRetriesExceededErrorMessageTest.methodUnderTest(UniqueMaxRetriesExceededErrorMessageTest.java:29)

                Reason: could not generate a sufficient number of values

                 -> Generation was abandoned after 1000 attempts to avoid an infinite loop.

                Possible causes:

                 -> filter() predicate rejected too many generated values, exceeding the retry limit
                 -> withUnique() method unable to generate a sufficient number of values

                Selector target: class Boolean (depth=1)

                 │ Path to root:
                 │   <1:Boolean>
                 │    └──<0:List>   <-- Root
                 │
                 │ Format: <depth:class: field>

                """;
    }
}
