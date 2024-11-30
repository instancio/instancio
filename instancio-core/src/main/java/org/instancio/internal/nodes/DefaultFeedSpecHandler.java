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
package org.instancio.internal.nodes;

import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.generator.Generator;
import org.instancio.internal.context.SelectorMap;
import org.instancio.internal.feed.InternalFeedSpecResolver;
import org.instancio.internal.selectors.FeedSelectors;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;

import java.util.Set;

final class DefaultFeedSpecHandler implements InternalFeedSpecHandler {

    private static final InternalFeedSpecHandler NOOP_HANDLER = node -> {
    };

    private final NodeContext nodeContext;
    private final SelectorMap<Feed> feedSelectorMap;

    private DefaultFeedSpecHandler(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        this.feedSelectorMap = nodeContext.getFeedSelectorMap();
    }

    static InternalFeedSpecHandler create(final NodeContext nodeContext) {
        final SelectorMap<Feed> map = nodeContext.getFeedSelectorMap();
        return map.isEmpty() ? NOOP_HANDLER : new DefaultFeedSpecHandler(nodeContext);
    }

    @Override
    public void applyFeedSpecs(final InternalNode node) {
        if (node.isIgnored() || (!node.is(NodeKind.POJO) && !node.is(NodeKind.RECORD))) {
            return;
        }
        final Feed feed = feedSelectorMap.getValue(node).orElse(null);
        if (feed == null) {
            return;
        }

        final InternalFeedSpecResolver specsResolver = new InternalFeedSpecResolver(feed);

        for (InternalNode child : node.getChildren()) {
            final FeedSpec<?> spec = specsResolver.getSpec(child);

            if (spec != null) {
                final TargetSelector selector = FeedSelectors.forProperty(child);
                nodeContext.putGenerator(selector, (Generator<?>) spec);
            }
        }

        final OnFeedPropertyUnmatched onFeedPropertyUnmatched =
                nodeContext.getSettings().get(Keys.ON_FEED_PROPERTY_UNMATCHED);

        final Set<String> unmappedProperties = specsResolver.getUnmappedFeedProperties();

        if (onFeedPropertyUnmatched == OnFeedPropertyUnmatched.FAIL
                && !unmappedProperties.isEmpty()) {

            throw Fail.withUsageError(ErrorMessageUtils.unmappedFeedProperties(
                    unmappedProperties, nodeContext.getSettings()));
        }
    }
}
