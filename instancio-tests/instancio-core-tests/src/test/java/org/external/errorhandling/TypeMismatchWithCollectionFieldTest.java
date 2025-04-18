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
import org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger;

import static org.instancio.Select.field;

class TypeMismatchWithCollectionFieldTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(TwoListsOfInteger.class)
                .set(field(TwoListsOfInteger::getList1), "invalid arg")
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.TypeMismatchWithCollectionFieldTest.methodUnderTest(TypeMismatchWithCollectionFieldTest.java:29)

                Reason: error assigning value to: field TwoListsOfInteger.list1 (depth=1)

                 │ Path to root:
                 │   <1:TwoListsOfInteger: List<Integer> list1>
                 │    └──<0:TwoListsOfInteger>   <-- Root
                 │
                 │ Format: <depth:class: field>


                Type mismatch:

                 -> Target field .............: List<Integer> list1 (in org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger)
                 -> Provided argument type ...: String
                 -> Provided argument value ..: "invalid arg"

                """;
    }
}
