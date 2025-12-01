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
package org.instancio.internal.feed;

import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Set;

public class InternalFeedSpecResolver {

    private final InternalFeed feed;
    private final Set<String> unmappedFeedProperties;

    public InternalFeedSpecResolver(final Feed feed) {
        this.feed = InternalFeedProxy.asInternalFeed(feed);
        this.unmappedFeedProperties = this.feed.getFeedProperties();
    }

    public Set<String> getUnmappedFeedProperties() {
        return unmappedFeedProperties;
    }

    /**
     * Resolves a {@link FeedSpec} for the given {@code node}:
     *
     * <ol>
     *   <li>see if the {@link Feed} subclass has a matching property</li>
     *   <li>if not, fallback to a matching data property, if any</li>
     * </ol>
     *
     * @param node to resolve the spec for
     * @return the resolved feed spec or {@code null} if no matches found
     */
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public @Nullable FeedSpec<?> getSpec(final InternalNode node) {
        final Class<?> feedClass = feed.getFeedContext().getFeedClass();
        final String nodeFieldName = node.getField().getName();
        final boolean nodeMatchedFeedProperty = unmappedFeedProperties.remove(nodeFieldName);
        final Method customFeedSpecMethod = ReflectionUtils.getZeroArgMethod(feedClass, nodeFieldName);

        if (customFeedSpecMethod != null) {
            return feed.createSpec(new SpecMethod(customFeedSpecMethod), /*args=*/ null);
        }

        // If no explicit FeedSpec method defined, then attempt
        // to map the field name to a property in the data source
        return nodeMatchedFeedProperty
                ? feed.createSpec(nodeFieldName, node.getTargetClass())
                : null;
    }
}
