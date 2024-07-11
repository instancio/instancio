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
import org.instancio.test.support.pojo.feed.FeedWithTag;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedProviderTagKeyValueTest {

    @Test
    void tagKeyAndValue() {
        final List<FeedWithTag> results = Instancio.ofList(FeedWithTag.class)
                .applyFeed(all(FeedWithTag.class), source -> source.ofResource(FeedWithTag.CSV_FILE)
                        .onDataEnd(FeedDataEndAction.RECYCLE)
                        .withTagKey("id")
                        .withTagValue("203"))
                .create();

        assertThat(results)
                .isNotEmpty()
                .extracting(FeedWithTag::getField1)
                .containsOnly("Д_203");
    }
}
