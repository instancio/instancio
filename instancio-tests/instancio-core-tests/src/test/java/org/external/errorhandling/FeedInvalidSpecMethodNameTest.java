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
import org.instancio.feed.FeedSpec;

class FeedInvalidSpecMethodNameTest extends AbstractErrorMessageTestTemplate {

    @Feed.Source(string = "id, value\n1, value")
    private interface SampleFeed extends Feed {
        FeedSpec<Integer> invalidName();
    }

    @Override
    void methodUnderTest() {
        Instancio.createFeed(SampleFeed.class)
                .invalidName()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedInvalidSpecMethodNameTest.methodUnderTest(FeedInvalidSpecMethodNameTest.java:33)

                Reason: unmatched spec method 'invalidName()' declared by the feed class

                 -> org.external.errorhandling.FeedInvalidSpecMethodNameTest$SampleFeed

                The property name 'invalidName' does not map to any property in the data:

                 -> [id, value]

                """;
    }

}
