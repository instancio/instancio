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
import org.instancio.feed.FunctionProvider;

class FeedFunctionSpecComponentMethodNotFoundTest extends AbstractErrorMessageTestTemplate {

    @Feed.Source(string = "x\n123")
    private interface SampleFeed extends Feed {

        @FunctionSpec(params = "propertyDoesNotExist", provider = Provider.class)
        FeedSpec<Integer> shouldFail();

        class Provider implements FunctionProvider {
            @SuppressWarnings("unused")
            static Integer sample(Integer any) {
                return -1;
            }
        }
    }

    @Override
    void methodUnderTest() {
        Instancio.createFeed(SampleFeed.class)
                .shouldFail()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedFunctionSpecComponentMethodNotFoundTest.methodUnderTest(FeedFunctionSpecComponentMethodNotFoundTest.java:43)

                Reason: invalid method name 'propertyDoesNotExist' specified by the `@FunctionSpec.params` attribute in feed class:

                 -> org.external.errorhandling.FeedFunctionSpecComponentMethodNotFoundTest$SampleFeed

                To resolve this error:

                 -> Check the annotation attributes of the following method:

                  │ FeedSpec<Integer> shouldFail();

                """;
    }

}
