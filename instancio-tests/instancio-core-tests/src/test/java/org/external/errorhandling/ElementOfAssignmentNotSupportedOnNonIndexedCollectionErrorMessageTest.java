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

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.test.support.pojo.collections.sets.SetInteger;

import static org.instancio.Select.elementOf;

class ElementOfAssignmentNotSupportedOnNonIndexedCollectionErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(SetInteger.class)
                .assign(Assign.valueOf(elementOf(SetInteger::getSet).first())
                        .to(elementOf(SetInteger::getSet).last()))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.ElementOfAssignmentNotSupportedOnNonIndexedCollectionErrorMessageTest.methodUnderTest(ElementOfAssignmentNotSupportedOnNonIndexedCollectionErrorMessageTest.java:31)

                Reason: elementOf() in assign() requires an ordered, index-addressable container (a List or array),
                but the target is of type Set:

                field SetInteger.set (depth=1)

                 │ Path to root:
                 │   <1:SetInteger: Set<Integer> set>
                 │    └──<0:SetInteger>   <-- Root
                 │
                 │ Format: <depth:class: field>


                assign() identifies elements by position - first(), last(), at(index) - and copies a value
                between them. Set provides no positional element access, so neither the source nor the
                destination element can be resolved.

                Offending selector:

                 -> elementOf(SetInteger::getSet).last()

                To resolve this error:

                 -> Use set() or generate() instead of assign() to populate elements directly, e.g.

                    set(elementOf(Example::getItems).field(Item::getValue), "value")
                    generate(elementOf(Example::getItems), gen -> gen.ints().range(1, 10))

                """;
    }
}
