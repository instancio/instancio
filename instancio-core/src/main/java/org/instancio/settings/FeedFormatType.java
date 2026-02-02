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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.feed.Feed;

/**
 * Enumeration of data format types supported by the
 * {@link Feed.Source} annotation.
 *
 * @see Feed
 * @see Keys#FEED_FORMAT_TYPE
 * @since 5.0.0
 */
@ExperimentalApi
public enum FeedFormatType {

    /**
     * CSV format.
     *
     * @since 5.0.0
     */
    CSV,

    /**
     * JSON format.
     *
     * <p>Note that using JSON requires
     * <a href="https://github.com/FasterXML/jackson-databind">Jackson Databind</a>
     * to be present on the classpath.
     *
     * @since 5.0.0
     */
    JSON
}
