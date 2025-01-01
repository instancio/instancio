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
import org.instancio.test.support.pojo.collections.lists.ListInteger;

import static org.instancio.Select.allInts;

class TypeMismatchWithCollectionElementTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(ListInteger.class)
                .set(allInts(), "invalid arg")
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.TypeMismatchWithCollectionElementTest.methodUnderTest(TypeMismatchWithCollectionElementTest.java:29)

                Reason: error adding element to collection: field ListInteger.list (depth=1)

                 │ Path to root:
                 │   <1:ListInteger: List<Integer> list>
                 │    └──<0:ListInteger>   <-- Root
                 │
                 │ Format: <depth:class: field>


                Type mismatch:

                 -> Target type ..............: Integer
                 -> Provided argument type ...: String
                 -> Provided argument value ..: "invalid arg"

                """;
    }
}
