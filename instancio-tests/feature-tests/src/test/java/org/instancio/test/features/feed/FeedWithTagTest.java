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

import lombok.AllArgsConstructor;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.feed.FeedWithTag;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.FEED, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class FeedWithTagTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM)
            .set(Keys.FEED_TAG_KEY, "tag");

    @Feed.Source(resource = FeedWithTag.CSV_FILE)
    private interface SampleFeed extends Feed {
        FeedSpec<String> id();

        FeedSpec<String> field1();

        FeedSpec<String> field2();
    }

    @Feed.TagKey("tag")
    @Feed.Source(resource = FeedWithTag.CSV_FILE)
    private interface SampleFeedWithTagKeyAnnotation extends SampleFeed {}

    @Data
    @AllArgsConstructor
    private static final class Entry {
        private final String id, field1, field2;
    }

    private static final Set<Entry> EN_ENTRIES = CollectionUtils.asSet(
            new Entry("101", "D_101", "F_101"),
            new Entry("102", "D_102", "F_102"),
            new Entry("103", "D_103", "F_103"));

    private static final Set<Entry> RU_ENTRIES = CollectionUtils.asSet(
            new Entry("201", "Д_201", "Ф_201"),
            new Entry("202", "Д_202", "Ф_202"),
            new Entry("203", "Д_203", "Ф_203"));

    private static final Set<Entry> ALL_ENTRIES = Stream
            .concat(EN_ENTRIES.stream(), RU_ENTRIES.stream())
            .collect(toSet());

    private static Stream<Arguments> tagArgs() {
        return Stream.of(
                Arguments.of("EN", EN_ENTRIES),
                Arguments.of("RU", RU_ENTRIES),
                Arguments.of(null, ALL_ENTRIES));
    }

    @MethodSource("tagArgs")
    @ParameterizedTest
    void usingGenerate_ofSet(final String tag, final Set<Entry> expectedEntries) {
        final SampleFeed result = Instancio.ofFeed(SampleFeed.class)
                .withTagValue(tag)
                .create();

        final Set<Entry> results = Instancio.ofSet(Entry.class)
                .size(expectedEntries.size())
                .generate(field(Entry::getId), result.id())
                .generate(field(Entry::getField1), result.field1())
                .generate(field(Entry::getField2), result.field2())
                .create();

        assertThat(results).isEqualTo(expectedEntries);
    }

    @MethodSource("tagArgs")
    @ParameterizedTest
    void usingGenerate_ofStream(final String tag, final Set<Entry> expectedEntries) {
        final SampleFeed result = Instancio.ofFeed(SampleFeed.class)
                .withTagValue(tag)
                .create();

        final Set<Entry> results = Instancio.of(Entry.class)
                .generate(field(Entry::getId), result.id())
                .generate(field(Entry::getField1), result.field1())
                .generate(field(Entry::getField2), result.field2())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DDD)
                .collect(toSet());

        assertThat(results).isEqualTo(expectedEntries);
    }

    @MethodSource("tagArgs")
    @ParameterizedTest
    void newSpecInstancePerObject(final String tag, final Set<Entry> expectedEntries) {
        final Set<Entry> results = Stream.generate(() -> {
                    // create each entry using a new Spec instance
                    final SampleFeed spec = Instancio.ofFeed(SampleFeed.class)
                            .withTagValue(tag)
                            .create();

                    return createAndAssertEntry(spec);
                })
                .limit(Constants.SAMPLE_SIZE_DDD)
                .collect(toSet());

        assertThat(results).isEqualTo(expectedEntries);
    }

    @MethodSource("tagArgs")
    @ParameterizedTest
    void reusingSpecInstance(final String tag, final Set<Entry> expectedEntries) {
        // create each entry using the same Spec instance
        final SampleFeed spec = Instancio.ofFeed(SampleFeed.class)
                .withTagValue(tag)
                .create();

        final Set<Entry> results = Stream.generate(() -> createAndAssertEntry(spec))
                .limit(Constants.SAMPLE_SIZE_DDD)
                .collect(toSet());

        assertThat(results).isEqualTo(expectedEntries);
    }

    private static Entry createAndAssertEntry(final SampleFeed spec) {
        final Entry entry = new Entry(spec.id().get(), spec.field1().get(), spec.field2().get());
        assertThat(entry.field1).endsWith(entry.id);
        assertThat(entry.field2).endsWith(entry.id);
        return entry;
    }

    /**
     * Verify using an arbitrary property (id in this case) as the tag key.
     */
    @ValueSource(strings = {"101", "102", "103", "201", "202", "203"})
    @ParameterizedTest
    void withTagKey_viaSettings(final String id) {
        final SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.FEED_TAG_KEY, "id")
                .withTagValue(id)
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_D; i++) {
            assertThat(feed.field1().get()).endsWith(id);
            assertThat(feed.field2().get()).endsWith(id);
        }
    }

    @ValueSource(strings = {"101", "102", "103", "201", "202", "203"})
    @ParameterizedTest
    void withTagKey(final String id) {
        final SampleFeed feed = Instancio.ofFeed(SampleFeedWithTagKeyAnnotation.class)
                // verify that withTagKey() takes precedence over settings
                .withSetting(Keys.FEED_TAG_KEY, "any-value")
                .withTagKey("id")
                .withTagValue(id)
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_D; i++) {
            assertThat(feed.field1().get()).endsWith(id);
            assertThat(feed.field2().get()).endsWith(id);
        }
    }

    /**
     * Verify that incomplete records, where tag key column is missing,
     * do not result in an error.
     */
    @Test
    void tagKeyMissingForSomeRecords() {
        final String data = "a,b,c\n"
                + "10,11,12\n"
                + "20"; // missing c value

        final Feed result = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofString(data))
                .dataAccess(FeedDataAccess.SEQUENTIAL)
                .onDataEnd(FeedDataEndAction.RECYCLE)
                .withTagKey("c")
                .create();

        final FeedSpec<Integer> spec = result.intSpec("c");

        for (int i = 0; i < 5; i++) {
            assertThat(spec.get()).isEqualTo(12);
            assertThat(spec.get()).isNull(); // should return null for missing column
        }
    }
}
