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

import org.instancio.internal.ApiValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableList;

public abstract class AbstractDataStore<R> implements DataStore<R> {

    private final List<R> data;
    private final String tagKey;
    private final Map<String, Integer> fieldIndexMap;
    private final List<String> tagKeys;
    private final Map<String, List<R>> groupedByTag;

    protected AbstractDataStore(final String tagKey, final List<R> data) {
        ApiValidator.isFalse(data.isEmpty(), "empty data source");
        this.data = unmodifiableList(data);
        this.tagKey = tagKey;
        this.fieldIndexMap = createFieldIndexMap(data);
        this.groupedByTag = groupDataByTag(tagKey, fieldIndexMap, data);
        this.tagKeys = unmodifiableList(new ArrayList<>(groupedByTag.keySet()));
    }

    protected abstract Map<String, Integer> createFieldIndexMap(List<R> data);

    protected abstract Map<String, List<R>> groupDataByTag(
            String tagKey,
            Map<String, Integer> fieldIndexMap,
            List<R> data);

    protected final List<R> getData() {
        return data;
    }

    @Override
    public final List<R> get(final String tagValue) {
        final List<R> tagData = groupedByTag.get(tagValue);
        ApiValidator.notNull(tagData, () -> String.format(
                "no data found with tag value: '%s' (tagKey is set to: '%s')", tagValue, tagKey));
        return tagData;
    }

    @Override
    public final int indexOf(final String propertyName) {
        return fieldIndexMap.getOrDefault(propertyName, -1);
    }

    @Override
    public final boolean contains(final String propertyName) {
        return fieldIndexMap.containsKey(propertyName);
    }

    @Override
    public final Set<String> getPropertyKeys() {
        return fieldIndexMap.keySet();
    }

    @Override
    public final List<String> getTagKeys() {
        return tagKeys;
    }
}
