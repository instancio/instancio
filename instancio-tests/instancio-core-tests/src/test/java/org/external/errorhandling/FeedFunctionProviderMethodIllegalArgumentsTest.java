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
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FunctionProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedFunctionProviderMethodIllegalArgumentsTest {

    @Feed.Source(string = "x\n1")
    private interface SampleFeed extends Feed {

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

    @Test
    void methodUnderTest() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);
        final FeedSpec<Integer> spec = feed.fromX();

        assertThatThrownBy(spec::get)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("""
                        Reason: exception thrown by spec method 'fromX()' declared by the feed class:

                         -> org.external.errorhandling.FeedFunctionProviderMethodIllegalArgumentsTest$SampleFeed

                        The error was caused by calling the method declared by FeedFunctionProviderMethodIllegalArgumentsTest$SampleFunction:

                          │ List<Integer> processX(String, Long, List<String>) {
                          │     ...
                          │ }
                        """)
                // Note: the IllegalArgumentException message varies with Java version,
                // therefore, we cannot do an equality assertion for the entire message
                // as it will fail when tests are run against JDK 17+.
                .hasMessageContaining("Root cause: java.lang.IllegalArgumentException")
                .hasMessageContaining("""
                        To resolve this error:

                         -> Verify that the spec properties specified by the '@FunctionSpec.params' attribute
                            match the number of parameters and parameter types defined by the 'processX' method

                         -> Ensure the method does not throw an exception
                        """);
    }
}
