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
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;

import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

class AmbiguousAssigmentErrorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Address.class)
                .assign(Assign.given(allStrings()).is("foo").set(field(Phone::getNumber), "bar"))
                .create();
    }

    @Override
    protected Class<?> expectedException() {
        return UnresolvedAssignmentException.class;
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.AmbiguousAssigmentErrorTest.methodUnderTest(AmbiguousAssigmentErrorTest.java:33)

                Reason: ambiguous assignment expression

                 -> The origin selector 'all(String)' matches multiple values.
                    It's not clear which of these values should be used:

                 -> Match 1: field Address.street

                    <1:Address: String street>
                     └──<0:Address>

                 -> Match 2: field Address.city

                    <1:Address: String city>
                     └──<0:Address>

                Format: <depth:class: field>

                There could be more matches. Evaluation stopped after the second match.
                To print the node hierarchy, run Instancio in verbose() mode:

                  Instancio.of(Example.class)
                      // snip ...
                      .verbose()
                      .create();

                To resolve the error, consider narrowing down the origin selector
                so that it matches only one target. This can be done using:

                 -> Scopes
                    https://www.instancio.org/user-guide/#selector-scopes

                 -> Depth
                    https://www.instancio.org/user-guide/#selector-depth

                """;
    }
}
