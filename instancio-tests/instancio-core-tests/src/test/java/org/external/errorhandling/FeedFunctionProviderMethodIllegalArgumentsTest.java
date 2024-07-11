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
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FunctionProvider;

import java.util.List;

class FeedFunctionProviderMethodIllegalArgumentsTest extends AbstractErrorMessageTestTemplate {

    @Feed.Source(string = "x\n1")
    private interface SampleFeed extends Feed {
        FeedSpec<Integer> x();

        @FunctionSpec(params = {"x"}, provider = SampleFunction.class)
        FeedSpec<Integer> fromX();
    }

    private static class SampleFunction implements FunctionProvider {
        // parameters do not match the FunctionSpec
        @SuppressWarnings("unused")
        List<Integer> processX(String x, Long l, List<String> z) {
            return null;
        }
    }

    @Override
    void methodUnderTest() {
        Instancio.createFeed(SampleFeed.class)
                .fromX()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.FeedFunctionProviderMethodIllegalArgumentsTest.methodUnderTest(FeedFunctionProviderMethodIllegalArgumentsTest.java:47)

                Reason: exception thrown by spec method 'fromX()' declared by the feed class:

                 -> org.external.errorhandling.FeedFunctionProviderMethodIllegalArgumentsTest$SampleFeed

                The error was caused by calling the method declared by FeedFunctionProviderMethodIllegalArgumentsTest$SampleFunction:

                  │ List<Integer> processX(String, Long, List<String>) {
                  │     ...
                  │ }

                Root cause: java.lang.IllegalArgumentException: wrong number of arguments: 1 expected: 3

                To resolve this error:

                 -> Verify that the spec properties specified by the '@FunctionSpec.params' attribute
                    match the number of parameters and parameter types defined by the 'processX' method

                 -> Ensure the method does not throw an exception

                """;
    }

}
