/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.settings.FeedFormatType;

class FeedJacksonNotOnClasspathErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Feed.FormatType(FeedFormatType.JSON)
    @Feed.Source(string = "[{}]")
    private interface SampleFeed extends Feed {}

    @Override
    void methodUnderTest() {
        Instancio.createFeed(SampleFeed.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedJacksonNotOnClasspathErrorMessageTest.methodUnderTest(FeedJacksonNotOnClasspathErrorMessageTest.java:30)

                Reason: JSON feeds require 'jackson-databind' (not included with Instancio) to be on the classpath

                To resolve this error:

                 -> Add the following dependency:

                    https://central.sonatype.com/artifact/com.fasterxml.jackson.core/jackson-databind

                """;
    }
}
