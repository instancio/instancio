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
package org.instancio.internal.feed.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.feed.DataSource;
import org.instancio.internal.feed.DataLoader;
import org.instancio.internal.util.Fail;

import java.util.ArrayList;
import java.util.List;

import static org.instancio.internal.util.ErrorMessageUtils.jacksonNotOnClasspathErrorMessage;

public final class JsonDataLoader implements DataLoader<List<JsonNode>> {

    @Override
    public List<JsonNode> load(final DataSource dataSource) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(getInputStream(dataSource));
            final List<JsonNode> results = new ArrayList<>(jsonNode.size());
            for (int i = 0; i < jsonNode.size(); i++) {
                results.add(jsonNode.get(i));
            }
            return results;
        } catch (NoClassDefFoundError error) {
            throw Fail.withUsageError(jacksonNotOnClasspathErrorMessage(), error);
        }
    }
}
