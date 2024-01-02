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
package org.instancio.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PrimitiveWrapperBiLookup {

    private static final Map<Class<?>, Class<?>> CORE_TYPES = Collections.unmodifiableMap(initCoreTypesMap());

    private static Map<Class<?>, Class<?>> initCoreTypesMap() {
        final Map<Class<?>, Class<?>> map = new HashMap<>();
        map.put(boolean.class, Boolean.class);
        map.put(char.class, Character.class);
        map.put(byte.class, Byte.class);
        map.put(short.class, Short.class);
        map.put(int.class, Integer.class);
        map.put(long.class, Long.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        // lookup both ways
        map.put(Boolean.class, boolean.class);
        map.put(Character.class, char.class);
        map.put(Byte.class, byte.class);
        map.put(Short.class, short.class);
        map.put(Integer.class, int.class);
        map.put(Long.class, long.class);
        map.put(Float.class, float.class);
        map.put(Double.class, double.class);
        return map;
    }

    public static Class<?> getEquivalent(final Class<?> primitiveOrWrapper) {
        return CORE_TYPES.get(primitiveOrWrapper);
    }

    private PrimitiveWrapperBiLookup() {
        // non-instantiable
    }
}
