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
package org.instancio.test.features.feed;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedSourceNotFoundTest {

    private static final String INVALID_RESOURCE = "does/not/exist/file.csv";

    @Feed.Source(resource = INVALID_RESOURCE)
    private interface SampleFeed extends Feed {}

    @Test
    void notFound() {
        assertThatThrownBy(() -> Instancio.createFeed(SampleFeed.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Reason: failed loading feed data")
                .hasMessageContaining("Source: %s", INVALID_RESOURCE)
                .hasMessageContaining("null InputStream");
    }
}
