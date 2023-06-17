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

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import java.time.LocalDate;

import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
class AssignPredicateClassCastExceptionTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringFields.class)
                .assign(Assign.given(StringFields::getOne)
                        .satisfies((LocalDate d) -> d != null) // wrong type - expected String
                        .set(field(StringFields::getTwo), "two"))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.AssignPredicateClassCastExceptionTest.methodUnderTest(AssignPredicateClassCastExceptionTest.java:37)

                Reason: error invoking the predicate against generated object of type java.lang.String

                """;
    }
}
