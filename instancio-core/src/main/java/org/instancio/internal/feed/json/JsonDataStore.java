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
package org.instancio.internal.feed.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.instancio.internal.feed.AbstractDataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonDataStore extends AbstractDataStore<JsonNode> {

    public JsonDataStore(final String tagKey, final List<JsonNode> data) {
        super(tagKey, data);
    }

    @Override
    protected Map<String, Integer> createFieldIndexMap(final List<JsonNode> data) {
        final Map<String, Integer> fieldIndexMap = new LinkedHashMap<>();
        final JsonNode firstRecord = data.get(0);
        final Set<Map.Entry<String, JsonNode>> headers = firstRecord.properties();
        int i = 0;
        for (Map.Entry<String, JsonNode> entry : headers) {
            fieldIndexMap.put(entry.getKey(), i);
            i++;
        }
        return fieldIndexMap;
    }

    @Override
    protected Map<String, List<JsonNode>> groupDataByTag(
            final String tagKey,
            final Map<String, Integer> fieldIndexMap,
            final List<JsonNode> data) {

        final Map<String, List<JsonNode>> map = new HashMap<>();

        for (JsonNode entry : data) {
            final JsonNode node = entry.get(tagKey);
            final String tag = node == null ? null : node.textValue();

            List<JsonNode> tagData = map.get(tag);

            //noinspection Java8MapApi
            if (tagData == null) {
                tagData = new ArrayList<>();
                map.put(tag, tagData);
            }
            tagData.add(entry);
        }
        return map;
    }

    @Override
    public JsonNode get(final int index) {
        return getData().get(index);
    }

    @Override
    public int size() {
        return getData().size();
    }
}
