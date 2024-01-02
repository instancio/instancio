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
package org.instancio.testsupport.utils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeMapBuilder {

    private final Map<TypeVariable<?>, Type> typeMap = new HashMap<>();
    private final Class<?> klass;

    private TypeMapBuilder(Class<?> klass) {
        this.klass = klass;
    }

    public static TypeMapBuilder forClass(Class<?> klass) {
        return new TypeMapBuilder(klass);
    }

    public TypeMapBuilder with(String typeVariable, Type mappedType) {
        typeMap.put(TypeUtils.getTypeVar(klass, typeVariable), mappedType);
        return this;
    }

    public Map<TypeVariable<?>, Type> get() {
        return typeMap;
    }
}
