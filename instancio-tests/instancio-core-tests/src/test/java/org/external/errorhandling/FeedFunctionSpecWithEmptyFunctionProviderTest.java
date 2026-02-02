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
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FunctionProvider;

class FeedFunctionSpecWithEmptyFunctionProviderTest extends AbstractErrorMessageTestTemplate {

    /**
     * A provider with no functions.
     */
    private static class EmptyFunctionProvider implements FunctionProvider {}

    @Feed.Source(string = "x,y\n1,2")
    private interface SampleFeed extends Feed {

        FeedSpec<Integer> x();

        FeedSpec<Integer> y();

        @FunctionSpec(params = {"x", "y"}, provider = EmptyFunctionProvider.class)
        FeedSpec<Integer> xAndY();
    }

    @Override
    void methodUnderTest() {
        Instancio.createFeed(SampleFeed.class)
                .xAndY()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedFunctionSpecWithEmptyFunctionProviderTest.methodUnderTest(FeedFunctionSpecWithEmptyFunctionProviderTest.java:45)

                Reason: the following FunctionProvider implementation should contain exactly one method:

                 -> org.external.errorhandling.FeedFunctionSpecWithEmptyFunctionProviderTest$EmptyFunctionProvider

                The provider is referenced by:

                  │ FeedSpec<Integer> xAndY();

                declared by the feed class:

                 -> org.external.errorhandling.FeedFunctionSpecWithEmptyFunctionProviderTest$SampleFeed

                To resolve this error:

                 -> Verify that the provider class contains exactly one method.

                 -> Ensure the number of parameters and parameter types match the
                    parameters declared by the '@SpecFunction.params' attribute.


                """;
    }

}
