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

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FunctionProvider;
import org.instancio.feed.PostProcessor;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedWithStringMapperTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE);

    @Feed.Source(string = "a,b,c,d,e,f\nA,B,C,D,E,F")
    private interface SampleFeed extends Feed {

        @WithStringMapper(ToByteArrayMapper.class)
        FeedSpec<byte[]> a();

        @WithStringMapper(ToByteArrayMapper.class)
        @DataSpec("b")
        FeedSpec<byte[]> b();

        @WithStringMapper(ToSingletonSetMapper.class)
        @DataSpec("c")
        FeedSpec<Set<String>> cAsSingletonSet();

        // The post-processor should be applied after the converter
        @WithPostProcessor(ToEmptyStringPostProcessor.class)
        @WithStringMapper(ToByteArrayMapper.class)
        @DataSpec("c")
        FeedSpec<byte[]> cAsEmptyByteArray();

        class ToEmptyStringPostProcessor implements PostProcessor<byte[]> {
            @Override
            public byte[] process(final byte[] input, final Random random) {
                return new byte[0];
            }
        }

        // The mapper is not applied to Template, Function, and Generated specs

        @WithStringMapper(ToByteArrayMapper.class)
        @TemplateSpec("${d}")
        FeedSpec<String> templateSpec();

        @WithStringMapper(ToByteArrayMapper.class)
        @FunctionSpec(params = "e", provider = IdentityFunction.class)
        FeedSpec<String> functionSpec();

        @WithStringMapper(ToByteArrayMapper.class)
        @GeneratedSpec(FGenerator.class)
        FeedSpec<String> generatedSpec();

        class FGenerator implements Generator<String> {
            @Override
            public String generate(final Random random) {
                return "F";
            }
        }

        class IdentityFunction implements FunctionProvider {
            @SuppressWarnings("unused")
            String getValue(String input) {
                return input;
            }
        }

        class ToByteArrayMapper implements Function<String, byte[]> {
            @Override
            public byte[] apply(final String s) {
                return s.getBytes();
            }
        }

        class ToSingletonSetMapper implements Function<String, Set<String>> {
            @Override
            public Set<String> apply(final String s) {
                return Collections.singleton(s);
            }
        }
    }

    @Test
    void mapper() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        assertThat(new String(feed.a().get())).isEqualTo("A");
        assertThat(new String(feed.b().get())).isEqualTo("B");
        assertThat(new String(feed.cAsEmptyByteArray().get())).isEmpty();
        assertThat(feed.cAsSingletonSet().get()).containsExactly("C");

        // converters are not applied to these specs
        assertThat(feed.templateSpec().get()).isEqualTo("D");
        assertThat(feed.functionSpec().get()).isEqualTo("E");
        assertThat(feed.generatedSpec().get()).isEqualTo("F");
    }
}
