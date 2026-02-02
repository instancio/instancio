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
package org.instancio.test.features.feed;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.FEED, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class FeedWithUniqueTest {

    @Feed.Source(string = "id,value\n1,v1\n2,v2\n3,v3\n4,v4\n5,v5")
    private interface SampleFeed extends Feed {
        int NUMBER_OF_FEED_ENTRIES = 5;

        FeedSpec<String> id();

        FeedSpec<String> value();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withUnique() {
        @Data
        class Pojo {
            String id, value;
        }

        final TargetSelector uniqueSelector = Instancio.gen()
                .oneOf(field(Pojo::getId), field(Pojo::getValue))
                .get();

        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        final List<Pojo> results = Instancio.ofList(Pojo.class)
                .size(SampleFeed.NUMBER_OF_FEED_ENTRIES)
                .withUnique(uniqueSelector)
                .generate(field(Pojo::getId), feed.id())
                .generate(field(Pojo::getValue), feed.value())
                .create();

        assertThat(results)
                .hasSize(SampleFeed.NUMBER_OF_FEED_ENTRIES)
                .allSatisfy(pojo -> assertThat(pojo.value).endsWith(pojo.id));
    }
}
