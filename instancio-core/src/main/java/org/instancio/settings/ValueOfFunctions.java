/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

final class ValueOfFunctions {

    private static final Map<Class<?>, Function<String, Object>> VALUE_OF_FUNCTIONS = createMap();

    private static Map<Class<?>, Function<String, Object>> createMap() {
        final Map<Class<?>, Function<String, Object>> fnMap = new HashMap<>();
        fnMap.put(Boolean.class, Boolean::valueOf);
        fnMap.put(Byte.class, Byte::valueOf);
        fnMap.put(Short.class, Short::valueOf);
        fnMap.put(Integer.class, Integer::valueOf);
        fnMap.put(Long.class, Long::valueOf);
        fnMap.put(Float.class, Float::valueOf);
        fnMap.put(Double.class, Double::valueOf);
        return Collections.unmodifiableMap(fnMap);
    }

    static Function<String, Object> getFunction(final Class<?> type) {
        return VALUE_OF_FUNCTIONS.get(type);
    }

    private ValueOfFunctions() {
        // non-instantiable
    }
}
