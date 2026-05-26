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
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;

import static org.instancio.Select.field;

class InvalidSizeTargetTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .size(field(Pet::getName), 2)
                .size(field(Person::getAge), 3)
                .create();
    }

    @Override
    String expectedMessage() {
        return """
                
                
                Error creating an object
                 -> at org.external.errorhandling.InvalidSizeTargetTest.methodUnderTest(InvalidSizeTargetTest.java:31)
                
                Reason: 'size()' can only be applied to a collection, array, or map.
                
                Found selector(s) targeting an incompatible type:
                
                 -> field(Person::getAge)
                    at org.external.errorhandling.InvalidSizeTargetTest.methodUnderTest(InvalidSizeTargetTest.java:30)
                
                 -> field(Pet::getName)
                    at org.external.errorhandling.InvalidSizeTargetTest.methodUnderTest(InvalidSizeTargetTest.java:29)
                
                
                """;
    }
}
