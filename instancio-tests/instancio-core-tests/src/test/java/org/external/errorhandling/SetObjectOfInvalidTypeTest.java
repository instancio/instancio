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
import org.instancio.test.support.pojo.person.Person;

import static org.instancio.Select.field;

class SetObjectOfInvalidTypeTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .set(field("name"), 123)
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SetObjectOfInvalidTypeTest.methodUnderTest(SetObjectOfInvalidTypeTest.java:29)

                Reason: error assigning value to: field Person.name (depth=1)

                 │ Path to root:
                 │   <1:Person: String name>
                 │    └──<0:Person>   <-- Root
                 │
                 │ Format: <depth:class: field>


                Type mismatch:

                 -> Target field .............: String name (in org.instancio.test.support.pojo.person.Person)
                 -> Provided argument type ...: Integer
                 -> Provided argument value ..: 123

                Root cause:
                 -> java.lang.IllegalArgumentException: Can not set java.lang.String field org.instancio.test.support.pojo.person.Person.name to java.lang.Integer

                """;
    }
}
