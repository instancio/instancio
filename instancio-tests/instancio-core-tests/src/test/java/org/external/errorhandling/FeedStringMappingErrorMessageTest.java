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
import org.instancio.feed.Feed;

class FeedStringMappingErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        final Feed feed = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofString("value\nfoo"))
                .create();

        feed.intSpec("value").get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedStringMappingErrorMessageTest.methodUnderTest(FeedStringMappingErrorMessageTest.java:29)

                Reason: error mapping String value to target type

                 -> Property name ..: "value"
                 -> Value ..........: "foo"

                Root cause: java.lang.NumberFormatException: For input string: "foo"

                """;
    }

}
