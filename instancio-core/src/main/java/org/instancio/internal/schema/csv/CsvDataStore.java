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
package org.instancio.internal.schema.csv;

import org.instancio.internal.schema.DataStore;
import org.instancio.internal.util.Fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public class CsvDataStore implements DataStore<List<String[]>> {

    private final Map<String, Integer> fieldIndexMap;
    private final List<String> availableTags;
    private final Map<String, List<String[]>> dataByTag;

    public CsvDataStore(final List<String[]> data, final String tagProperty) {
        this.fieldIndexMap = unmodifiableMap(getFieldIndexMap(data));
        this.dataByTag = unmodifiableMap(groupDataByTag(tagProperty, fieldIndexMap, data));
        this.availableTags = unmodifiableList(new ArrayList<>(dataByTag.keySet()));
    }

    private static Map<String, Integer> getFieldIndexMap(final List<String[]> data) {
        if (data.isEmpty()) {
            throw Fail.withUsageError("empty Schema data source");
        }
        final String[] headers = data.get(0);
        final Map<String, Integer> fieldIndexMap = new LinkedHashMap<>();

        for (int i = 0; i < headers.length; i++) {
            fieldIndexMap.put(headers[i], i);
        }
        return fieldIndexMap;
    }

    private static Map<String, List<String[]>> groupDataByTag(
            final String tagProperty,
            final Map<String, Integer> fieldIndexMap,
            final List<String[]> data) {

        final Map<String, List<String[]>> map = new HashMap<>();
        final int tagIndex = fieldIndexMap.getOrDefault(tagProperty, -1);

        // start from 1 to skip column headers
        for (int i = 1; i < data.size(); i++) {
            final String[] row = data.get(i);
            final String tag = tagIndex == -1 ? null : row[tagIndex];

            List<String[]> tagData = map.get(tag);

            //noinspection Java8MapApi
            if (tagData == null) {
                tagData = new ArrayList<>();
                map.put(tag, tagData);
            }
            tagData.add(row);
        }
        return map;
    }

    @Override
    public List<String[]> get(final String tagValue) {
        return dataByTag.get(tagValue);
    }

    @Override
    public int indexOf(final String propertyName) {
        return fieldIndexMap.getOrDefault(propertyName, -1);
    }

    @Override
    public boolean contains(final String propertyName) {
        return fieldIndexMap.containsKey(propertyName);
    }

    @Override
    public Collection<String> getPropertyKeys() {
        return fieldIndexMap.keySet();
    }

    @Override
    public List<String> getAvailableTags() {
        return availableTags;
    }

    @Override
    public int fieldCount() {
        return fieldIndexMap.size();
    }
}
