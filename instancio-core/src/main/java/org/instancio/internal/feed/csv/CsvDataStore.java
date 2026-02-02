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
package org.instancio.internal.feed.csv;

import org.instancio.internal.feed.AbstractDataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvDataStore extends AbstractDataStore<String[]> {

    public CsvDataStore(final String tagKey, final List<String[]> data) {
        super(tagKey, data);
    }

    @Override
    protected Map<String, Integer> createFieldIndexMap(final List<String[]> data) {
        final String[] headers = data.get(0);
        final Map<String, Integer> fieldIndexMap = new LinkedHashMap<>();

        for (int i = 0; i < headers.length; i++) {
            fieldIndexMap.put(headers[i], i);
        }
        return fieldIndexMap;
    }

    @Override
    protected Map<String, List<String[]>> groupDataByTag(
            final String tagKey,
            final Map<String, Integer> fieldIndexMap,
            final List<String[]> data) {

        final Map<String, List<String[]>> map = new HashMap<>();
        final int tagIndex = fieldIndexMap.getOrDefault(tagKey, -1);

        // start from 1 to skip column headers
        for (int i = 1; i < data.size(); i++) {
            final String[] row = data.get(i);
            final String tag = tagIndex == -1 || tagIndex >= row.length
                    ? null
                    : row[tagIndex];

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
    public String[] get(final int index) {
        // offset by one to exclude the header row
        return getData().get(index + 1);
    }

    @Override
    public int size() {
        // exclude header row
        return getData().size() - 1;
    }
}
