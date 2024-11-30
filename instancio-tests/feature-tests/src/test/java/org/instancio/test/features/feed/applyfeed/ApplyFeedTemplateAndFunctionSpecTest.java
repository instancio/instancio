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
package org.instancio.test.features.feed.applyfeed;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FunctionProvider;
import org.instancio.feed.PostProcessor;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedTemplateAndFunctionSpecTest {

    @SuppressWarnings("unused")
    @Feed.Source(string = "a,b\na1,b1\na2,b2\na3,b3\na4,b4")
    private interface StringFieldsFeed extends Feed {

        @DataSpec("a")
        @WithPostProcessor(UnderscoreAppender.class)
        FeedSpec<String> one();

        @DataSpec("b")
        @WithPostProcessor(UnderscoreAppender.class)
        FeedSpec<String> two();

        @WithPostProcessor(UnderscoreAppender.class)
        @TemplateSpec("three_${one}")
        FeedSpec<String> three();

        @WithPostProcessor(UnderscoreAppender.class)
        @FunctionSpec(params = {"one", "two"}, provider = Hyphenator.class)
        FeedSpec<String> four();

        class Hyphenator implements FunctionProvider {
            String hyphenate(final String one, final String two) {
                return one + "-" + two;
            }
        }

        class UnderscoreAppender implements PostProcessor<String> {
            @Override
            public String process(final String input, final Random random) {
                return "_" + input;
            }
        }
    }

    @Test
    void shouldPopulateObjectsUsingDeclaredFeedSpecMethods() {
        final Feed feed = Instancio.ofFeed(StringFieldsFeed.class)
                .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                .create();

        final List<StringFields> results = Instancio.ofList(StringFields.class)
                .size(4)
                .applyFeed(all(StringFields.class), feed)
                .withSetting(Keys.ON_FEED_PROPERTY_UNMATCHED, OnFeedPropertyUnmatched.IGNORE)
                .create();

        assertThat(results)
                .extracting(StringFields::getOne)
                .containsExactly("_a1", "_a2", "_a3", "_a4");

        assertThat(results)
                .extracting(StringFields::getTwo)
                .containsExactly("_b1", "_b2", "_b3", "_b4");

        assertThat(results)
                .extracting(StringFields::getThree)
                .containsExactly("_three__a1", "_three__a2", "_three__a3", "_three__a4");

        assertThat(results)
                .extracting(StringFields::getFour)
                .containsExactly("__a1-_b1", "__a2-_b2", "__a3-_b3", "__a4-_b4");
    }
}
