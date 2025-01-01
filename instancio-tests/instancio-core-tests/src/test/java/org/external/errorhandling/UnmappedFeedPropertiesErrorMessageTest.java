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
import org.instancio.test.support.pojo.basic.StringHolder;

import static org.instancio.Select.root;

class UnmappedFeedPropertiesErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringHolder.class)
                .applyFeed(root(), feed -> feed.ofString("""
                        property2, property1, property3
                        x, y, z
                        """))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.UnmappedFeedPropertiesErrorMessageTest.methodUnderTest(UnmappedFeedPropertiesErrorMessageTest.java:32)

                Reason: unmapped feed properties

                 -> The feed specified in the applyFeed() method contains the following
                    properties that do not map to any field in the target class:

                    property1, property2, property3

                The error was triggered because of the following setting:

                 -> Keys.ON_FEED_PROPERTY_UNMATCHED: OnFeedPropertyUnmatched.FAIL

                To resolve this error, consider one of the following:

                 -> Ensure that each property in the feed has a corresponding field in the target class

                 -> Update the Keys.ON_FEED_PROPERTY_UNMATCHED setting to: OnFeedPropertyUnmatched.IGNORE
                    if the unmatched properties are intentional

                """;
    }
}