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
import org.instancio.test.support.pojo.person.Person;

import static org.instancio.Select.setter;

class InvalidMethodNameSelectorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .ignore(setter(Person.class, "setNamex"))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.InvalidMethodNameSelectorTest.methodUnderTest(InvalidMethodNameSelectorTest.java:28)

                Reason: Could not find method method 'setNamex' declared by class org.instancio.test.support.pojo.person.Person

                """;
    }

}
