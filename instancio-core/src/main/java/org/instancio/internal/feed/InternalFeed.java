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

import org.instancio.documentation.InternalApi;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.internal.util.Sonar;

import java.util.Set;

@InternalApi
public interface InternalFeed {

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    InternalFeedContext<?> getFeedContext();

    /**
     * Returns all the properties defined in the data store.
     * Note that these properties may or may not be explicitly
     * declared by a {@link Feed} subclass.
     */
    Set<String> getDataProperties();

    /**
     * Returns a spec for the given invocation of a {@code method}
     * declared by the feed.
     *
     * @param method the feed method that was invoked
     * @param args   method arguments (if any)
     * @param <T>    the type of the spec to return
     * @return a spec with the specified type
     */
    @SuppressWarnings("PMD.UseVarargs")
    <T> FeedSpec<T> createSpec(SpecMethod method, Object[] args);

    /**
     * Returns a spec for the given property name and target type.
     *
     * @param propertyName the name of the property
     * @param targetType   the target type of the property
     * @param <T>          the type of the spec to return
     * @return a spec with the specified type
     */
    <T> FeedSpec<T> createSpec(String propertyName, Class<T> targetType);

}
