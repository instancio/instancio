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
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;

class FeedEndOfDataTest extends AbstractErrorMessageTestTemplate {

    @Feed.Source(string = "id\n1")
    private interface SampleFeed extends Feed {}

    @Override
    void methodUnderTest() {
        final SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.FEED_DATA_ACCESS, FeedDataAccess.SEQUENTIAL)
                .create();

        feed.stringSpec("id").get(); // 1
        feed.stringSpec("id").get(); // error
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedEndOfDataTest.methodUnderTest(FeedEndOfDataTest.java:35)

                Reason: reached end of data for feed class

                 -> org.external.errorhandling.FeedEndOfDataTest$SampleFeed

                The error was triggered because of the following settings:

                 -> Keys.FEED_DATA_ACCESS ......: FeedDataAccess.SEQUENTIAL
                 -> Keys.FEED_DATA_END_ACTION ..: FeedDataEndAction.FAIL

                To resolve this error:

                 -> Ensure the data source provides a sufficient number of records
                 -> Set Keys.FEED_DATA_END_ACTION to FeedDataEndAction.RECYCLE
                 -> Set Keys.FEED_DATA_ACCESS to FeedDataAccess.RANDOM

                Note that the last two options may result in duplicate values being produced.

                """;
    }

}
