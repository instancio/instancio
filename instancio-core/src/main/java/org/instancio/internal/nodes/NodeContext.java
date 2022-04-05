/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal.nodes;

import org.instancio.internal.reflection.DeclaredAndInheritedFieldsCollector;
import org.instancio.internal.reflection.FieldCollector;

import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeContext {

    private final FieldCollector fieldCollector = new DeclaredAndInheritedFieldsCollector();
    private final Set<Node> visited = new HashSet<>();
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Map<Class<?>, Class<?>> subtypeMap;

    public NodeContext(
            final Map<TypeVariable<?>, Class<?>> rootTypeMap,
            final Map<Class<?>, Class<?>> subtypeMap) {

        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
        this.subtypeMap = Collections.unmodifiableMap(subtypeMap);
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }

    public Map<Class<?>, Class<?>> getSubtypeMap() {
        return subtypeMap;
    }

    public void visited(final Node node) {
        visited.add(node);
    }

    public boolean isUnvisited(final Node node) {
        return !visited.contains(node);
    }

    public FieldCollector getFieldCollector() {
        return fieldCollector;
    }
}
