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
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedDataSourceAbsolutePathTest {

    @Test
    void absolutePath() {
        final Path path = Instancio.gen().nio().path()
                .tmp()
                .createFile(new ByteArrayInputStream("value\nfoo".getBytes()))
                .get();

        assertThat(path).exists();

        final Feed result = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofFile(path))
                .create();

        assertThat(result.stringSpec("value").get()).isEqualTo("foo");
    }

    @Feed.Source(file = "/tmp/sample-feed-absolute-path-test.csv")
    private interface SampleFeed extends Feed {
        FeedSpec<String> value();
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void feedSourceAnnotationWithAbsolutePath() {
        // Note: once created, the file will not be overwritten!
        final Path path = Instancio.gen().nio().path()
                .tmp()
                .name(random -> "sample-feed-absolute-path-test.csv")
                .createFile(new ByteArrayInputStream("value\nfoo".getBytes()))
                .get();

        assertThat(path).exists();

        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        assertThat(result.value().get()).isEqualTo("foo");
    }
}
