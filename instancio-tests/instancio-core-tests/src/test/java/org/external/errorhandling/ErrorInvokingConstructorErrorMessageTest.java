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
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;

class ErrorInvokingConstructorErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @SuppressWarnings("unused")
    private static class ValidatingConstructor {
        private final String value;

        ValidatingConstructor(final String value) {
            throw new IllegalArgumentException("invalid value: " + value);
        }
    }

    @Override
    void methodUnderTest() {
        Instancio.of(ValidatingConstructor.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.ErrorInvokingConstructorErrorMessageTest.methodUnderTest(ErrorInvokingConstructorErrorMessageTest.java:37)

                Reason: failed instantiating an object via constructor for node:

                class ErrorInvokingConstructorErrorMessageTest$ValidatingConstructor (depth=0)

                 │ Path to root:
                 │   <0:ErrorInvokingConstructorErrorMessageTest$ValidatingConstructor>   <-- Root
                 │
                 │ Format: <depth:class: field>

                 -> Constructor: org.external.errorhandling.ErrorInvokingConstructorErrorMessageTest$ValidatingConstructor(java.lang.String)

                This error was thrown because the Keys.ON_CONSTRUCTOR_ERROR setting is set to OnConstructorError.FAIL

                To resolve this error:

                 -> ensure generated values satisfy the constructor's validation logic
                 -> set Keys.ON_CONSTRUCTOR_ERROR to OnConstructorError.FALLBACK to fall back to instantiation without a constructor
                 -> remove InstantiationStrategy.ALL_ARGS from Keys.INSTANTIATION_STRATEGIES to disable instantiation via this constructor

                """;
    }
}
