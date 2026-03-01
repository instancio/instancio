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
package org.instancio.junit.given;

import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.generator.Generator;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GivenParameterWithSettingsSeedAnnotationTest {

    private static final long SEED = -1;

    private static final Set<Object> results = new HashSet<>();

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SEED, SEED);

    /**
     * Two arguments of the same type should have different
     * values even though the same seed value is used.
     */
    @RepeatedTest(10)
    void method1(
            @Given final UUID generatedValue1,
            @Given final UUID generatedValue2,
            @Given final SampleFeed feed) {

        addAndAssertResults(generatedValue1, generatedValue2, feed.uuid().get());
    }

    @ParameterizedTest
    @ValueSource(strings = "foo")
    void method2(
            final String string,
            @Given final UUID generatedValue1,
            @Given final UUID generatedValue2,
            @Given final SampleFeed feed) {

        assertThat(string).isEqualTo("foo");
        addAndAssertResults(generatedValue1, generatedValue2, feed.uuid().get());
    }

    private static void addAndAssertResults(
            final UUID generatedValue1,
            final UUID generatedValue2,
            final UUID generatedValue3) {

        assertThat(generatedValue1).isNotNull();
        assertThat(generatedValue2).isNotNull();
        assertThat(generatedValue3).isNotNull();

        results.add(generatedValue1);
        results.add(generatedValue2);
        results.add(generatedValue3);

        assertThat(results).hasSize(3);
    }

    @Feed.Source(string = "dummy\nsource")
    private interface SampleFeed extends Feed {

        @GeneratedSpec(UUIDGenerator.class)
        FeedSpec<UUID> uuid();

        class UUIDGenerator implements Generator<UUID> {
            @Override
            public UUID generate(final @NonNull Random random) {
                final String uuid = String.format("%s-%s-%s-%s-%s",
                        random.digits(8),
                        random.digits(4),
                        random.digits(4),
                        random.digits(4),
                        random.digits(12));
                return UUID.fromString(uuid);
            }
        }
    }
}
