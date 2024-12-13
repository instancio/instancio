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
package org.instancio.test.features.feed;

import org.instancio.Instancio;
import org.instancio.InstancioFeedApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedFormatType;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedErrorHandlingTest {

    /**
     * Contains feed spec methods that have no converters available
     * out-of-the-box for the specified return types.
     */
    @Feed.FormatType(FeedFormatType.CSV)
    @Feed.Source(string = "value\nfoo")
    private interface SampleFeed extends Feed {

        @SuppressWarnings("UnusedReturnValue")
        FeedSpec<Item<String>> value();

        @SuppressWarnings("UnusedReturnValue")
        @AliasSpec("value")
        FeedSpec<byte[]> unmappableType();
    }

    @Test
    void feedSpecWithUnsupportedReturnType() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        final FeedSpec<Item<String>> spec = result.value();

        assertThatThrownBy(spec::get)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("cannot map value 'foo' (unsupported target type: org.instancio.test.support.pojo.generics.basic.Item)");
    }

    @Test
    void feedSpecValueConversionError() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        final FeedSpec<byte[]> spec = result.unmappableType();

        assertThatThrownBy(spec::get)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("cannot map value 'foo' (unsupported target type: byte[])");
    }

    @Feed.Source
    private interface FeedWithEmptyPath extends Feed {}

    @Test
    void feedSourceAnnotationWithNoAttributes() {
        assertThatThrownBy(() -> Instancio.createFeed(FeedWithEmptyPath.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("no data source provided");
    }

    @Test
    void feedWithEmptyDataSource() {
        final InstancioFeedApi<Feed> api = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofString(""));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("empty data source");
    }
}
