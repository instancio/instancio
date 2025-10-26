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
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.person.Person;

class OnMaxDepthReachedErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .withMaxDepth(1)
                .withSetting(Keys.FAIL_ON_MAX_DEPTH_REACHED, true)
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.OnMaxDepthReachedErrorMessageTest.methodUnderTest(OnMaxDepthReachedErrorMessageTest.java:29)

                Reason: max depth reached while generating object graph

                field Person.finalField (depth=1)

                 │ Path to root:
                 │   <1:Person: String finalField>
                 │    └──<0:Person>   <-- Root
                 │
                 │ Format: <depth:class: field>

                 -> Generation stopped at depth 1
                    (configured using the Keys.MAX_DEPTH setting)

                This error was thrown because:

                 -> Keys.FAIL_ON_MAX_DEPTH_REACHED = true

                To resolve this error:

                 -> Increase the value of the Keys.MAX_DEPTH setting
                 -> Use withMaxDepth() to override the depth for this model
                 -> Set Keys.FAIL_ON_MAX_DEPTH_REACHED to false to skip creating deeper objects

                """;
    }

}
