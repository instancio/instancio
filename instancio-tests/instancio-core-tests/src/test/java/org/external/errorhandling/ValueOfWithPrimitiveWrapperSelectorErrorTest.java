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

import org.instancio.Assign;

import static org.instancio.Select.allLongs;

class ValueOfWithPrimitiveWrapperSelectorErrorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Assign.given(allLongs());
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.ValueOfWithPrimitiveWrapperSelectorErrorTest.methodUnderTest(ValueOfWithPrimitiveWrapperSelectorErrorTest.java:26)

                Reason: assignment origin must not be a primitive/wrapper selector such as allLongs()

                Please specify the type explicitly, for example: 'all(Long.class)' or 'all(long.class)'

                """;
    }
}
