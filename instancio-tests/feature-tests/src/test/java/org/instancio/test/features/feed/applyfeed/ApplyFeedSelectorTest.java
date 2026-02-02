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

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.FEED,
        Feature.APPLY_FEED,
        Feature.SELECTOR,
        Feature.PREDICATE_SELECTOR
})
@ExtendWith(InstancioExtension.class)
class ApplyFeedSelectorTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE);

    private static final String EXPECTED_VALUE = "_foo_";

    @SuppressWarnings("unused")
    @Feed.Source(string = "value\n" + EXPECTED_VALUE)
    private interface SampleFeed extends Feed {}

    //@formatter:off
    // Note: 'value' field maps to the corresponding feed property,
    // but 'unmapped' does not (should generate a random value)
    @Data private static class Outer { String value; String unmapped; Mid mid; }
    @Data private static class Mid   { String value; String unmapped; Inner inner; }
    @Data private static class Inner { String value; String unmapped; }
    @Data private static class Container { Outer outer; Mid mid; Inner inner; }
    //@formatter:on

    private static Stream<Arguments> outerSelectors() {
        return Stream.of(
                Arguments.of(root()),
                Arguments.of(all(Outer.class)),
                Arguments.of(types().of(Outer.class))
        );
    }

    @MethodSource("outerSelectors")
    @ParameterizedTest
    void applyFeedToOuter(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Outer result = Instancio.of(Outer.class)
                .applyFeed(selector, feed)
                .create();

        assertThat(result.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.mid.value).is(Conditions.RANDOM_STRING);
        assertThat(result.mid.inner.value).is(Conditions.RANDOM_STRING);
        assertUnmappedFieldHasARandomValue(result);
    }

    private static Stream<Arguments> midSelectors() {
        return Stream.of(
                Arguments.of(all(Mid.class)),
                Arguments.of(field(Outer::getMid)),
                Arguments.of(fields().named("mid")),
                Arguments.of(types().of(Mid.class)),
                // This selector all fields declared by Outer,
                // but the non-matching fields are simply ignored
                Arguments.of(fields().declaredIn(Outer.class))
        );
    }

    @MethodSource("midSelectors")
    @ParameterizedTest
    void applyFeedToMid(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Outer result = Instancio.of(Outer.class)
                .applyFeed(selector, feed)
                .create();

        assertThat(result.value).is(Conditions.RANDOM_STRING);
        assertThat(result.mid.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.mid.inner.value).is(Conditions.RANDOM_STRING);
        assertUnmappedFieldHasARandomValue(result);
    }

    private static Stream<Arguments> innerSelectors() {
        return Stream.of(
                Arguments.of(all(Inner.class)),
                Arguments.of(field(Mid::getInner)),
                Arguments.of(fields().named("inner")),
                Arguments.of(types().of(Inner.class))
        );
    }

    @MethodSource("innerSelectors")
    @ParameterizedTest
    void applyFeedToInner(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Outer result = Instancio.of(Outer.class)
                .applyFeed(selector, feed)
                .create();

        assertThat(result.value).is(Conditions.RANDOM_STRING);
        assertThat(result.mid.value).is(Conditions.RANDOM_STRING);
        assertThat(result.mid.inner.value).isEqualTo(EXPECTED_VALUE);
        assertUnmappedFieldHasARandomValue(result);
    }

    private static Stream<Arguments> allSelectors() {
        return Stream.of(
                Arguments.of(all(all(Outer.class), all(Mid.class), all(Inner.class))),
                Arguments.of(all(all(Outer.class), field(Outer::getMid), field(Mid::getInner))),
                Arguments.of(types()),
                Arguments.of(types(t -> t == Outer.class || t == Mid.class || t == Inner.class))
        );
    }

    @MethodSource("allSelectors")
    @ParameterizedTest
    void applyFeedToAll(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Outer result = Instancio.of(Outer.class)
                .applyFeed(selector, feed)
                .create();

        assertThat(result.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.mid.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.mid.inner.value).isEqualTo(EXPECTED_VALUE);
        assertUnmappedFieldHasARandomValue(result);
    }

    private static Stream<Arguments> withScopeSelectors() {
        return Stream.of(
                Arguments.of(all(Inner.class).within(scope(Mid.class))),
                Arguments.of(types().of(Inner.class).within(scope(Mid.class)))
        );
    }

    @MethodSource("withScopeSelectors")
    @ParameterizedTest
    void withScope(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Container result = Instancio.of(Container.class)
                .applyFeed(selector, feed)
                .create();

        assertThat(result.outer.mid.inner.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.mid.inner.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.inner.value).is(Conditions.RANDOM_STRING);
        assertUnmappedFieldHasARandomValue(result.outer);
    }


    private static Stream<Arguments> atDepthSelectors() {
        return Stream.of(
                // 0:Container -> 1:Outer -> 2:Mid -> 3:Inner
                Arguments.of(all(Inner.class).atDepth(3)),
                Arguments.of(types().of(Inner.class).atDepth(3)),
                Arguments.of(types().of(Inner.class).atDepth(d -> d > 2))
        );
    }

    @MethodSource("atDepthSelectors")
    @ParameterizedTest
    void atDepth(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Container result = Instancio.of(Container.class)
                .applyFeed(selector, feed)
                .create();

        assertThat(result.outer.mid.inner.value).isEqualTo(EXPECTED_VALUE);
        assertThat(result.mid.inner.value).is(Conditions.RANDOM_STRING);
        assertThat(result.inner.value).is(Conditions.RANDOM_STRING);
        assertUnmappedFieldHasARandomValue(result.outer);
    }

    private static void assertUnmappedFieldHasARandomValue(final Outer actual) {
        assertThat(actual.unmapped).is(Conditions.RANDOM_STRING);
        assertThat(actual.mid.unmapped).is(Conditions.RANDOM_STRING);
        assertThat(actual.mid.inner.unmapped).is(Conditions.RANDOM_STRING);
    }
}
