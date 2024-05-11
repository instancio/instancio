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

import static org.instancio.Select.allStrings;

class FilterPredicateExceptionErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(String.class)
                .set(allStrings(), "foo")
                .filter(allStrings(), obj -> {
                    throw new RuntimeException("expected error");
                })
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FilterPredicateExceptionErrorMessageTest.methodUnderTest(FilterPredicateExceptionErrorMessageTest.java:31)

                Reason: filter() predicate threw an exception
                 -> Value provided to predicate: "foo"
                 -> Target node: class String (depth=0)

                 │ Path to root:
                 │   <0:String>   <-- Root
                 │
                 │ Format: <depth:class: field>

                Root cause:
                 -> java.lang.RuntimeException: expected error

                """;
    }
}
