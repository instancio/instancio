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
import org.instancio.test.support.pojo.misc.AbcListHolder;

import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;

class ElementOfOriginRequiresElementOfDestinationErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(AbcListHolder.class)
                .assign(Assign.valueOf(elementOf(AbcListHolder::getAbcElements1).first())
                        .to(field(AbcListHolder::getAbc)))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.ElementOfOriginRequiresElementOfDestinationErrorMessageTest.methodUnderTest(ElementOfOriginRequiresElementOfDestinationErrorMessageTest.java:30)

                Reason: elementOf() origin requires an elementOf() destination

                An elementOf() origin reads a value from a specific element, so the value can
                only be copied into another element. The destination must therefore also be an
                elementOf() selector, but here it is not:

                 -> Origin ........: elementOf(AbcListHolder::getAbcElements1).first()
                 -> Destination ...: field(AbcListHolder::getAbc)

                To resolve this error:

                 -> Make the destination an elementOf() selector so the value can be copied
                    from one element to another, e.g.

                    assign(valueOf(elementOf(Example::getItems).at(0).field(Item::getValue))
                        .to(elementOf(Example::getItems).at(1).field(Item::getValue)))

                Note: the reverse direction is supported. A regular origin can be assigned to
                an elementOf() destination, copying a single value into each matched element, e.g.

                    assign(valueOf(field(Example::getValue))
                        .to(elementOf(Example::getItems).field(Item::getValue)))

                """;
    }
}
