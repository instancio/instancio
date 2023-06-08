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
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;

import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.When.valueOf;

class AmbiguousConditionalErrorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Address.class)
                .when(valueOf(allStrings()).is("foo").set(field(Phone::getNumber), "bar"))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.AmbiguousConditionalErrorTest.methodUnderTest(AmbiguousConditionalErrorTest.java:32)

                Reason: ambiguous conditional statement

                 -> The origin selector 'valueOf(all(String))' matches multiple values.
                    It's not clear which value the condition should be evaluated against:

                    -> Match 1: Node[Address.address, depth=1, type=String]
                       Ancestors:
                       -> Node[Address, depth=0, type=Address]

                    -> Match 2: Node[Address.city, depth=1, type=String]
                       Ancestors:
                       -> Node[Address, depth=0, type=Address]

                There could be more matches. Evaluation stopped after the second match.

                To resolve the error, consider narrowing down the origin selector
                so that it matches only one target. This can be done using:

                 -> Scopes
                    https://www.instancio.org/user-guide/#selector-scopes

                 -> Depth
                    https://www.instancio.org/user-guide/#selector-depth

                """;
    }
}
