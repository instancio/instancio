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
import org.instancio.internal.util.StringUtils;

/**
 * A setting that specifies what should happen if a feed property
 * is unmatched when using the {@code applyFeed()} method.
 *
 * <p>Note that this applies to both, properties in a data file and
 * feed spec methods defined by a {@link Feed} subclass.
 *
 * <p>For example, given the following CSV file, {@code SampleFeed},
 * and {@code SamplePojo}:
 *
 * <pre>
 * # sample.csv
 * property1, property2
 * foo,       bar
 * # ... snip
 * </pre>
 *
 * <pre>{@code
 * @Feed.Source(resource = "sample.csv")
 * interface SampleFeed extends Feed {
 *     FeedSpec<String> property3();
 * }
 *
 * class SamplePojo {
 *     String property1;
 * }
 * }</pre>
 *
 * <p>Then the following snippet:
 *
 * <pre>{@code
 * List<SamplePojo> pojos = Instancio.ofList(SamplePojo.class)
 *     .applyFeed(Select.all(SamplePojo.class), feed -> feed.of(SampleFeed.class))
 *     .create();
 * }</pre>
 *
 * <p>will throw an exception because {@code property2} from the data
 * and {@code property3} from {@code SampleFeed} do not match
 * any properties in the {@code SamplePojo}.
 *
 * @see Keys#ON_FEED_PROPERTY_UNMATCHED
 * @since 5.2.0
 */
@ExperimentalApi
public enum OnFeedPropertyUnmatched {

    /**
     * Ignore unmatched feed properties.
     *
     * @since 5.2.0
     */
    IGNORE,

    /**
     * Throw an exception if a feed property is unmatched.
     *
     * @since 5.2.0
     */
    FAIL;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
