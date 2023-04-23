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
import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.OnSetMethodNotFound;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

class MethodAssignerOnSetMethodNotFoundTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

        Instancio.of(WithoutSetter.class)
                .withSettings(settings)
                .create();
    }

    @Override
    String expectedMessage() {
        return """

                Throwing exception because:
                 -> Keys.ASSIGNMENT_TYPE = AssignmentType.METHOD
                 -> Keys.ON_SET_METHOD_NOT_FOUND = OnSetMethodNotFound.FAIL

                Setter method could not be resolved for field:
                 -> int MethodAssignerOnSetMethodNotFoundTest$WithoutSetter.value

                Using:
                 -> Keys.SETTER_STYLE = SetterStyle.SET
                 -> Expected method name: 'setValue'

                To resolve the error, consider one of the following:
                 -> Add the expected setter method
                 -> Update Keys.ON_SET_METHOD_NOT_FOUND setting to:
                    -> OnSetMethodNotFound.ASSIGN_FIELD to assign value via field
                    -> OnSetMethodNotFound.IGNORE to leave value uninitialised
                """;
    }

    @SuppressWarnings("unused")
    private static class WithoutSetter {
        private int value;
    }
}
