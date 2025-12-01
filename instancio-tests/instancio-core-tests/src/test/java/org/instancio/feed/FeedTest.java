/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.feed;

import org.instancio.Instancio;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class FeedTest {

    interface Foo<T> {}

    interface Bar {}

    interface Zaz {}

    @Feed.Source(string = "id\n123")
    private interface SampleFeed extends Foo<Integer>, Bar, Feed, Zaz {
        FeedSpec<Integer> id();
    }

    @Test
    void verifyToString() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        assertThat(result).hasToString("Proxy[org.instancio.feed.FeedTest$SampleFeed]");
    }

    @Test
    void verifyEqualsAndHashcode() {
        final SampleFeed result1 = Instancio.createFeed(SampleFeed.class);
        final SampleFeed result2 = Instancio.createFeed(SampleFeed.class);

        assertThat(result1)
                .isNotEqualTo(result2)
                .doesNotHaveSameHashCodeAs(result2);
        assertThat(result1.equals(result1)).isTrue();
    }

    @Test
    void create() {
        final SampleFeed result = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                .create();

        assertThat(result.stringSpec("id").get()).isEqualTo("123");

        assertThat(result.id().get()).isEqualTo(123);
    }

    @Test
    @SuppressWarnings({"resource", "InputStreamSlowMultibyteRead"})
    void shouldCloseDataInputStream() {
        class TestInputStream extends InputStream {
            final InputStream delegate = new ByteArrayInputStream("value\nfoo".getBytes());
            boolean closed;

            @Override
            public int read() throws IOException {
                return delegate.read();
            }

            @Override
            public void close() {
                closed = true;
            }
        }

        final TestInputStream inputStream = new TestInputStream();
        final Feed result = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofInputStream(inputStream))
                .create();

        assertThat(inputStream.closed).isTrue();
        assertThat(result.stringSpec("value").get()).isEqualTo("foo");
    }
}
