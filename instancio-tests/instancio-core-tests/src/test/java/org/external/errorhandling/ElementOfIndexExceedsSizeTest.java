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
import org.instancio.test.support.pojo.collections.lists.ListInteger;

import java.util.List;

import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;

class ElementOfIndexExceedsSizeTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(ListInteger.class)
                .size(all(List.class), 1)
                .set(elementOf(ListInteger::getList).at(5), 12345)
                .create();
    }

    @Override
    String expectedMessage() {
        return """
                
                
                Error creating an object
                 -> at org.external.errorhandling.ElementOfIndexExceedsSizeTest.methodUnderTest(ElementOfIndexExceedsSizeTest.java:33)
                
                Reason: elementOf() selector at index 5 requires at least 6 elements, but an explicit size of 1 was set.
                
                field ListInteger.list (depth=1)
                
                 │ Path to root:
                 │   <1:ListInteger: List<Integer> list>
                 │    └──<0:ListInteger>   <-- Root
                 │
                 │ Format: <depth:class: field>
                
                To resolve this error:
                
                 -> use a smaller index in the elementOf() selector
                 -> increase the size
                
                """;
    }
}
