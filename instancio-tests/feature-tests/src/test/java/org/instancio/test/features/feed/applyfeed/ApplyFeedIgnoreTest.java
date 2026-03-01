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
package org.instancio.test.features.feed.applyfeed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED, Feature.IGNORE})
@ExtendWith(InstancioExtension.class)
class ApplyFeedIgnoreTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE);

    @Feed.Source(string = "one,two\nfoo,bar")
    private interface SampleFeed extends Feed {}

    @Test
    void withIgnoredField() {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final StringFields result = Instancio.of(StringFields.class)
                .applyFeed(root(), feed)
                .ignore(field(StringFields::getOne))
                .create();

        assertThat(result.getOne()).isNull();
        assertThat(result.getTwo()).isEqualTo("bar");
        assertThat(result.getThree()).is(Conditions.RANDOM_STRING);
        assertThat(result.getFour()).is(Conditions.RANDOM_STRING);
    }

    @SuppressWarnings("NullAway")
    @Test
    void withIgnoredPojo() {
        class Container {
            StringFields pojo1, pojo2;
        }

        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Container result = Instancio.of(Container.class)
                .applyFeed(all(StringFields.class), feed)
                .ignore(field("pojo2"))
                .ignore(field(StringFields::getTwo))
                .create();

        assertThat(result.pojo1.getOne()).isEqualTo("foo");
        assertThat(result.pojo1.getTwo()).isNull();
        assertThat(result.pojo1.getThree()).is(Conditions.RANDOM_STRING);
        assertThat(result.pojo1.getFour()).is(Conditions.RANDOM_STRING);

        assertThat(result.pojo2).isNull();
    }

}
