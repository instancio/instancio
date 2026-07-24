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
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.test.support.pojo.constructor.StringsAbcCtor;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

/**
 * The same cycle as {@link UnresolvedAssignmentErrorTest}, but against a class
 * whose fields are assigned via constructor.
 */
class UnresolvedAssignmentWithConstructorErrorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringsAbcCtor.class)
                .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                .assign(valueOf(field(StringsAbcCtor::getB).within(scope(StringsAbcCtor.class))).to(StringsAbcCtor::getC))
                .assign(valueOf(StringsAbcCtor::getC).to(StringsAbcCtor::getA))
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
                 -> at org.external.errorhandling.UnresolvedAssignmentWithConstructorErrorTest.methodUnderTest(UnresolvedAssignmentWithConstructorErrorTest.java:38)
                
                Reason: unresolved assignment expression
                
                The following assignments could not be applied:
                
                 -> from [field(StringsAbcCtor::getC)] to [field(StringsAbcCtor::getA)]
                 -> from [field(StringsAbcCtor::getA)] to [field(StringsAbcCtor::getB)]
                 -> from [field(StringsAbcCtor::getB).within(scope(StringsAbcCtor))] to [field(StringsAbcCtor::getC)]
                
                As a result, the following targets could not be assigned a value:
                
                 -> class StringsAbcCtor (depth=0)
                
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
