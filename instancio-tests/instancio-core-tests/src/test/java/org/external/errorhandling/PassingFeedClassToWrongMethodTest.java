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
import org.instancio.feed.Feed;

class PassingFeedClassToWrongMethodTest extends AbstractErrorMessageTestTemplate {

    interface SampleFeed extends Feed {}

    @Override
    void methodUnderTest() {
        Instancio.create(SampleFeed.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.PassingFeedClassToWrongMethodTest.methodUnderTest(PassingFeedClassToWrongMethodTest.java:27)

                Reason: could not create an instance of interface org.external.errorhandling.PassingFeedClassToWrongMethodTest$SampleFeed

                Cause:

                 -> The specified class is an instance of org.instancio.feed.Feed

                To resolve this error:

                 -> Use the createFeed(Class) method:

                    ExampleFeed feed = Instancio.createFeed(ExampleFeed.class);

                 -> Or the builder API:

                    ExampleFeed feed = Instancio.of(ExampleFeed.class)
                        // snip ...
                        .create();


                """;
    }
}
