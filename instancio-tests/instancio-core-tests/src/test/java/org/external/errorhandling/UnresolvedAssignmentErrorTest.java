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
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.test.support.pojo.misc.StringsAbc;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

class UnresolvedAssignmentErrorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringsAbc.class)
                .assign(valueOf(StringsAbc::getA).to(StringsAbc::getB))
                .assign(valueOf(field(StringsAbc::getB).within(scope(StringsAbc.class))).to(StringsAbc::getC))
                .assign(valueOf(StringsAbc::getC).to(StringsAbc::getA))
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
                 -> at org.external.errorhandling.UnresolvedAssignmentErrorTest.methodUnderTest(UnresolvedAssignmentErrorTest.java:34)

                Reason: unresolved assignment expression

                The following assignments could not be applied:

                 -> from [field(StringsAbc, "c")] to [field(StringsAbc, "a")]
                 -> from [field(StringsAbc, "a")] to [field(StringsAbc, "b")]
                 -> from [field(StringsAbc, "b"), scope(StringsAbc)] to [field(StringsAbc, "c")]

                As a result, the following targets could not be assigned a value:

                 -> field StringsAbc.b (depth=1)
                 -> field StringsAbc.c (depth=1)
                 -> field StringsAbc.a (depth=1)

                Possible causes:

                 -> The assignments form a cycle, for example:

                    Pojo pojo = Instancio.of(Pojo.class)
                        .assign(Assign.valueOf(Pojo::getFoo).to(Pojo::getBar))
                        .assign(Assign.valueOf(Pojo::getBar).to(Pojo::getFoo))
                        .create();

                 -> Part of the assignment expression is ignored using the ignore() method:

                    Person person = Instancio.of(Person.class)
                        .ignore(field(Person::getGender)) // ignored!
                        .assign(Assign.given(field(Person::getGender), field(Person::getName))
                                .set(When.is(Gender.FEMALE), "Fiona")
                                .set(When.is(Gender.MALE), "Michael"))
                        .create();


                """;
    }
}
