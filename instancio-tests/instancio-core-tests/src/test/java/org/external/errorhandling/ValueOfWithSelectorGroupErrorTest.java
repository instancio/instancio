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

import org.instancio.Select;
import org.instancio.SelectorGroup;
import org.instancio.When;

import static org.instancio.Select.allStrings;

class ValueOfWithSelectorGroupErrorTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        final SelectorGroup group = Select.all(allStrings());
        When.valueOf(group);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.ValueOfWithSelectorGroupErrorTest.methodUnderTest(ValueOfWithSelectorGroupErrorTest.java:29)

                Reason: invalid origin selector

                Conditional origin must not match more than one target.Therefore origin selector cannot be a group such as:

                all(
                	all(String)
                )

                """;
    }
}
