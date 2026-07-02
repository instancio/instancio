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
package org.instancio.internal.nodes;

import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.generator.Generator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.context.SelectorMap;
import org.instancio.internal.feed.InternalFeedSpecResolver;
import org.instancio.internal.selectors.FeedSelectors;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class DefaultFeedSpecHandler implements InternalFeedSpecHandler {

    private static final InternalFeedSpecHandler NOOP_HANDLER = node -> {
    };

    private final ModelContext modelContext;
    private final SelectorMap<Feed> feedSelectorMap;

    private DefaultFeedSpecHandler(final ModelContext modelContext) {
        this.modelContext = modelContext;
        this.feedSelectorMap = modelContext.getFeedSelectorMap();
    }

    static InternalFeedSpecHandler create(final ModelContext modelContext) {
        final SelectorMap<Feed> map = modelContext.getFeedSelectorMap();
        return map.isEmpty() ? NOOP_HANDLER : new DefaultFeedSpecHandler(modelContext);
    }

    @Override
    public void applyFeedSpecs(final InternalNode node) {
        if (node.isStaticallyIgnored() || (!node.is(NodeKind.POJO) && !node.is(NodeKind.RECORD))) {
            return;
        }

        final List<SelectorMap.Match<Feed>> feeds = selectFeedsToApply(node);
        for (int i = feeds.size() - 1; i >= 0; i--) {
            applyFeed(node, feeds.get(i));
        }
    }

    /**
     * Returns the feeds to apply to the given node, in precedence order:
     * all matching {@code elementOf()} feeds followed by the first matching regular feed
     * ({@code getMatches} returns elementOf entries first - they have a higher priority).
     */
    private List<SelectorMap.Match<Feed>> selectFeedsToApply(final InternalNode node) {
        final List<SelectorMap.Match<Feed>> matches = feedSelectorMap.getMatches(node);
        final List<SelectorMap.Match<Feed>> results = new ArrayList<>(matches.size());
        boolean regularFeedIncluded = false;

        for (SelectorMap.Match<Feed> match : matches) {
            if (match.selector().matchesViaElementFrame()) {
                results.add(match);
            } else if (!regularFeedIncluded) {
                regularFeedIncluded = true;
                results.add(match);
            }
        }
        return results;
    }

    private void applyFeed(final InternalNode node, final SelectorMap.Match<Feed> match) {
        final InternalFeedSpecResolver specsResolver = new InternalFeedSpecResolver(match.value());

        for (InternalNode child : node.getChildren()) {
            final FeedSpec<?> spec = specsResolver.getSpec(child);

            if (spec != null) {
                final TargetSelector selector = propertySelector(match.selector(), child);
                modelContext.putGenerator(selector, (Generator<?>) spec);
            }
        }

        final OnFeedPropertyUnmatched onFeedPropertyUnmatched =
                modelContext.getSettings().get(Keys.ON_FEED_PROPERTY_UNMATCHED);

        final Set<String> unmappedProperties = specsResolver.getUnmappedFeedProperties();

        if (onFeedPropertyUnmatched == OnFeedPropertyUnmatched.FAIL
                && !unmappedProperties.isEmpty()) {

            throw Fail.withUsageError(ErrorMessageUtils.unmappedFeedProperties(
                    unmappedProperties, modelContext.getSettings()));
        }
    }

    private static TargetSelector propertySelector(
            final PredicateSelectorImpl feedSelector, final InternalNode child) {

        return feedSelector.matchesViaElementFrame()
                ? FeedSelectors.forElementProperty(feedSelector, child)
                : FeedSelectors.forProperty(child);
    }
}
