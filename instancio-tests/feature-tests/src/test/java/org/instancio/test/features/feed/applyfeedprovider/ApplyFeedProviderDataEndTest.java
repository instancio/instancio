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
package org.instancio.test.features.feed.applyfeedprovider;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedProviderDataEndTest {

    @Test
    void recycle() {
        final List<StringHolder> results = Instancio.ofList(StringHolder.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .applyFeed(all(StringHolder.class), source -> source.ofResource("data/FeedExample.csv")
                        .onDataEnd(FeedDataEndAction.RECYCLE)) // method under test
                .create();

        assertThat(results)
                .hasSize(Constants.SAMPLE_SIZE_DD)
                .extracting(StringHolder::getValue)
                .startsWith(
                        "value1", "value2", "value3",
                        "value1", "value2", "value3"
                        /* etc. values get recycled */);
    }
}
