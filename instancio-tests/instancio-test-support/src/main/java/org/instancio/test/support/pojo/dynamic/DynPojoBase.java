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
package org.instancio.test.support.pojo.dynamic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DynPojoBase {

    protected final Map<String, Object> data = new HashMap<>();

    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
    protected final <T> T get(String key) {
        return (T) data.get(key);
    }

    protected final void set(String key, Object value) {
        data.put(key, value);
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append("[");

        final String separator = ", ";
        data.forEach((k, v) -> {
            final String str = getValueAsString(v);

            sb
                    .append(System.lineSeparator())
                    .append("  ")
                    .append(k).append("=").append(str)
                    .append(separator);
        });

        sb.setLength(sb.length() - separator.length()); // remove last separator
        return sb.append("]").toString();
    }

    private static String getValueAsString(final Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().isArray()
                ? Arrays.toString((Object[]) obj)
                : obj.toString();
    }
}
