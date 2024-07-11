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
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedOptionalFieldsTest {

    @Feed.Source
    private interface FeedOptionalFields extends Feed {
        FeedSpec<String> a();

        FeedSpec<String> b();

        FeedSpec<String> c();
    }

    private static FeedOptionalFields createFeed(final String data) {
        return Instancio.ofFeed(FeedOptionalFields.class)
                .withDataSource(source -> source.ofString(data))
                .create();
    }

    @Test
    void optionalFirst() {
        final FeedOptionalFields result = createFeed("a,b,c\na-val,,");

        assertThat(result.a().get()).isEqualTo("a-val");
        assertThat(result.b().get()).isNull();
        assertThat(result.c().get()).isNull();
    }

    @Test
    void optionalSecond() {
        final FeedOptionalFields result = createFeed("a,b,c\n,b-val,");

        assertThat(result.a().get()).isNull();
        assertThat(result.b().get()).isEqualTo("b-val");
        assertThat(result.c().get()).isNull();
    }

    @Test
    void optionalLast() {
        final FeedOptionalFields result = createFeed("a,b,c\n,,c-val");

        assertThat(result.a().get()).isNull();
        assertThat(result.b().get()).isNull();
        assertThat(result.c().get()).isEqualTo("c-val");
    }
}
