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

class FeedWithNoDataSourceTest extends AbstractErrorMessageTestTemplate {

    interface SampleFeed extends Feed {}

    @Override
    void methodUnderTest() {
        Instancio.createFeed(SampleFeed.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedWithNoDataSourceTest.methodUnderTest(FeedWithNoDataSourceTest.java:27)

                Reason: no data source provided for feed org.external.errorhandling.FeedWithNoDataSourceTest$SampleFeed

                To resolve this error, please specify a data source using any of the options below.

                 -> Via the @Feed.Source annotation:

                    @Feed.Source(resource = "path/to/data/sample.csv")
                    interface SampleFeed extends Feed { ... }

                 -> Using the builder API:

                    import org.instancio.feed.DataSource;

                    SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
                        .withDataSource(myDataSource)
                        .create();

                """;
    }

}
