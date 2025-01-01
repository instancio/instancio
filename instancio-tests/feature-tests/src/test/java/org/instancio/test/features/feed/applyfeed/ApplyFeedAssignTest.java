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
package org.instancio.test.features.feed.applyfeed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class ApplyFeedAssignTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE);

    @Feed.Source(string = "one,two\nfoo,bar")
    private interface SampleFeed extends Feed {}

    @Test
    void assign() {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final StringFields result = Instancio.of(StringFields.class)
                .applyFeed(root(), feed)
                .assign(valueOf(StringFields::getOne).to(StringFields::getThree))
                .assign(valueOf(StringFields::getTwo).to(StringFields::getFour))
                .create();

        assertThat(result.getOne()).isEqualTo(result.getThree()).isEqualTo("foo");
        assertThat(result.getTwo()).isEqualTo(result.getFour()).isEqualTo("bar");
    }
}
