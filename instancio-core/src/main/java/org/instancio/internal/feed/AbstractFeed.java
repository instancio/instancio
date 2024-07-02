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
package org.instancio.internal.feed;

import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Keys;

import java.util.List;

public abstract class AbstractFeed implements InternalFeed {

    private final InternalFeedContext<?> feedContext;
    private final String tag;
    private final GeneratorContext generatorContext;

    protected AbstractFeed(final InternalFeedContext<?> feedContext) {
        this.feedContext = feedContext;
        this.generatorContext = feedContext.getGeneratorContext();
        this.tag = generatorContext.getSettings().get(Keys.FEED_TAG_VALUE);
    }

    @Override
    public final InternalFeedContext<?> getFeedContext() {
        return feedContext;
    }

    protected final GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    protected final String selectTag(List<String> availableTags) {
        return tag == null
                ? generatorContext.random().oneOf(availableTags)
                : tag;
    }
}
