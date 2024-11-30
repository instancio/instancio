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

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.PostProcessor;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedAdhocWithRootSelectorTest {

    @SuppressWarnings("unused")
    @Feed.Source(string = "givenName,surname,age,username,heightCm\nJohn,Doe,99,jdoe,178")
    private interface PersonFeed extends Feed {

        @DataSpec("givenName")
        FeedSpec<String> firstName();

        @DataSpec("surname")
        FeedSpec<String> lastName();

        @TemplateSpec("${firstName} ${lastName}")
        FeedSpec<String> fullName();

        @WithPostProcessor(IntegerNegator.class)
        FeedSpec<Integer> age();

        @DataSpec("heightCm")
        @WithPostProcessor(IntegerNegator.class)
        FeedSpec<Integer> height();

        @GeneratedSpec(EmailGenerator.class)
        FeedSpec<String> email();

        // Note: username() is not included as a spec method,
        // but it should still be populated in the target POJO
        // FeedSpec<String> username()

        class EmailGenerator implements Generator<String> {
            @Override
            public String generate(final Random random) {
                return "jdoe@example.com";
            }
        }

        class IntegerNegator implements PostProcessor<Integer> {
            @Override
            public Integer process(final Integer input, final Random random) {
                return -input;
            }
        }
    }

    @Test
    void verify() {
        final Feed feed = Instancio.createFeed(PersonFeed.class);

        final PersonPojo result = Instancio.of(PersonPojo.class)
                .applyFeed(root(), feed)
                .withSetting(Keys.ON_FEED_PROPERTY_UNMATCHED, OnFeedPropertyUnmatched.IGNORE)
                .create();

        assertThat(result.firstName).isEqualTo("John");
        assertThat(result.lastName).isEqualTo("Doe");
        assertThat(result.fullName).isEqualTo("John Doe");
        assertThat(result.age).isEqualTo(-99);
        assertThat(result.height).isEqualTo(-178);
        assertThat(result.email).isEqualTo("jdoe@example.com");
        assertThat(result.username).isEqualTo("jdoe");
    }

    @Data
    private static class PersonPojo {
        private String firstName;
        private String lastName;
        private String fullName;
        private Integer age;
        private Integer height;
        private String username;
        private String email;
    }
}
