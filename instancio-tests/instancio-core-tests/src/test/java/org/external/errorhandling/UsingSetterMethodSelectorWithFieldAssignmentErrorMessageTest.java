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
import org.instancio.test.support.pojo.misc.StringFields;

import static org.instancio.Select.setter;

class UsingSetterMethodSelectorWithFieldAssignmentErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringFields.class)
                .ignore(setter(StringFields::setOne))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.UsingSetterMethodSelectorWithFieldAssignmentErrorMessageTest.methodUnderTest(UsingSetterMethodSelectorWithFieldAssignmentErrorMessageTest.java:29)

                Reason: setter() selector cannot be used with AssignmentType.FIELD:
                 -> setter(StringFields, "setOne(String)")
                    at org.external.errorhandling.UsingSetterMethodSelectorWithFieldAssignmentErrorMessageTest.methodUnderTest(UsingSetterMethodSelectorWithFieldAssignmentErrorMessageTest.java:28)

                Root cause:

                Instancio provides the 'Keys.ASSIGNMENT_TYPE' setting that
                determines how objects are populated. The setting supports two options:

                 -> AssignmentType.FIELD (default behaviour)
                    Objects are populated via fields only. Setter methods are ignored.
                    This is the recommended setting for most uses cases.

                 -> AssignmentType.METHOD
                    Objects are populated via methods and (optionally) fields.

                Using setter() selectors is only supported with METHOD assignment.

                    // Example
                    Settings settings = Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);

                    Pojo pojo = Instancio.of(Pojo.class)
                        .withSettings(settings)
                        .set(setter(Pojo::setValue), "foo")
                        .create();

                See https://www.instancio.org/user-guide/#assignment-settings for details

                """;
    }
}
