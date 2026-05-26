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

import static org.instancio.Select.field;
import static org.instancio.Select.fields;

class InvalidSizeTargetWithPredicateSelectorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .size(fields(f -> f.getDeclaringClass() == Person.class), 3)
                .size(field(Person::getName), 2)
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.InvalidSizeTargetWithPredicateSelectorTest.methodUnderTest(InvalidSizeTargetWithPredicateSelectorTest.java:31)

                Reason: 'size()' can only be applied to a collection, array, or map.

                Found selector(s) targeting an incompatible type:

                 -> fields(Predicate<Field>)
                    at org.external.errorhandling.InvalidSizeTargetWithPredicateSelectorTest.methodUnderTest(InvalidSizeTargetWithPredicateSelectorTest.java:29)

                 -> field(Person::getName)
                    at org.external.errorhandling.InvalidSizeTargetWithPredicateSelectorTest.methodUnderTest(InvalidSizeTargetWithPredicateSelectorTest.java:30)


                """;
    }
}
