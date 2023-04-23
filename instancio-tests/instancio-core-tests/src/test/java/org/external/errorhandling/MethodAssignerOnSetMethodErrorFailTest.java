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
import org.instancio.assignment.OnSetMethodError;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.SetterErrorPojo;

import static org.instancio.Select.allInts;

class MethodAssignerOnSetMethodErrorFailTest extends AbstractErrorMessageTestTemplate {

    private static final int EXPECTED_VALUE = 123;

    @Override
    void methodUnderTest() {
        final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL);

        Instancio.of(SetterErrorPojo.class)
                .withSettings(settings)
                .set(allInts(), EXPECTED_VALUE)
                .create();
    }

    @Override
    String expectedMessage() {
        return String.format("""

                Throwing exception because:
                 -> Keys.ASSIGNMENT_TYPE = AssignmentType.METHOD
                 -> Keys.ON_SET_METHOD_ERROR = OnSetMethodError.FAIL

                Method invocation failed:

                 -> Method ...................: SetterErrorPojo.setValue(int)
                 -> Provided argument type ...: Integer
                 -> Provided argument value ..: %s

                Root cause:
                 -> java.lang.UnsupportedOperationException: expected exception from setter

                To resolve the error, consider one of the following:
                 -> Address the root cause that triggered the exception
                 -> Update Keys.ON_SET_METHOD_ERROR setting to
                    -> OnSetMethodError.ASSIGN_FIELD to assign value via field
                    -> OnSetMethodError.IGNORE to leave value uninitialised
                """, EXPECTED_VALUE);
    }
}
