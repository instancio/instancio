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
package org.instancio.feed;

import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.documentation.ExperimentalApi;

import java.io.InputStream;
import java.nio.file.Path;

import static org.instancio.internal.feed.InternalFeedContext.builder;

/**
 * Provider interface for building {@link Feed} instances.
 *
 * @since 5.0.0
 */
@ExperimentalApi
@FunctionalInterface
public interface FeedProvider {

    /**
     * Configures a {@link Feed} instance using the specified builder.
     *
     * <p>Refer to the {@link InstancioApi#applyFeed(TargetSelector, FeedProvider)}
     * Javadoc for sample usage.
     *
     * @param feed builder for configuring a feed instance
     * @return a configured feed
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations get(FeedBuilderFactory feed);

    /**
     * Factory for building instances of a {@link Feed}.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    interface FeedBuilderFactory {

        /**
         * Creates a feed instance of the specified feed class.
         *
         * @param feedClass the feed class
         * @param <F>       the type of the feed
         * @return API builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        default <F extends Feed> FeedOperations of(Class<F> feedClass) {
            return builder(feedClass);
        }

        /**
         * Creates a feed instance for the specified input stream.
         *
         * @param inputStream the input stream containing the data
         * @return API builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        default FeedOperations ofInputStream(InputStream inputStream) {
            return builder(source -> source.ofInputStream(inputStream));
        }

        /**
         * Creates a feed instance for the specified file path.
         *
         * @param path the path of the file containing the data
         * @return API builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        default FeedOperations ofFile(Path path) {
            return builder(source -> source.ofFile(path));
        }

        /**
         * Creates a feed instance for the specified classpath resource.
         *
         * @param name the name of the classpath resource containing the data
         * @return API builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        default FeedOperations ofResource(String name) {
            return builder(source -> source.ofResource(name));
        }

        /**
         * Creates a feed instance with the given data.
         *
         * @param data the data represented as a string
         * @return API builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        default FeedOperations ofString(String data) {
            return builder(source -> source.ofString(data));
        }
    }
}
