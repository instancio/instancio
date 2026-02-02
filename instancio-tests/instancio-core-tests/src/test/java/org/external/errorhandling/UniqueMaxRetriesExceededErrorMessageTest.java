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

                Reason: failed generating a value for node:

                class Boolean (depth=1)

                 │ Path to root:
                 │   <1:Boolean>
                 │    └──<0:List>   <-- Root
                 │
                 │ Format: <depth:class: field>

                 -> Generation was abandoned after 1000 attempts to avoid an infinite loop
                    (configured using the Keys.MAX_GENERATION_ATTEMPTS settings)

                Possible causes:

                 -> filter() predicate rejected too many generated values, exceeding the maximum number of attempts
                 -> withUnique() method unable to generate a sufficient number of unique values
                 -> a hash-based collection (a Set or Map) of a given size could not be populated

                To resolve this error:

                 -> update the generation parameters to address the root cause
                 -> increase the value of the Keys.MAX_GENERATION_ATTEMPTS setting

                """;
    }
}
