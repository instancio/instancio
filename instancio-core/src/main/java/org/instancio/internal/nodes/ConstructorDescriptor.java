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
package org.instancio.internal.nodes;

import org.instancio.internal.util.Sonar;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class ConstructorDescriptor {

    private final Constructor<?> constructor;
    private final List<Class<?>> parameterTypes;
    private final List<InternalNode> constructorParameterNodes;
    private final List<InternalNode> nonParameterChildren;

    /**
     * @param constructor    the constructor to invoke
     * @param parameterNodes node per constructor parameter, in declaration order;
     *                       each element is a field-backed child node of the owning node
     * @param children       the owning node's children created from class members
     */
    ConstructorDescriptor(
            final Constructor<?> constructor,
            final List<InternalNode> parameterNodes,
            final List<InternalNode> children) {

        this.constructor = constructor;
        // cache to avoid subsequent calls to constructor.getParameterTypes()
        this.parameterTypes = List.of(constructor.getParameterTypes());
        this.constructorParameterNodes = Collections.unmodifiableList(parameterNodes);

        // Compared by identity: a parameter node that maps to a field is the
        // very same instance as the corresponding child
        final Set<InternalNode> parameters = Collections.newSetFromMap(new IdentityHashMap<>());
        parameters.addAll(parameterNodes);

        final List<InternalNode> nonParameters = new ArrayList<>(children.size());
        for (InternalNode child : children) {
            if (!parameters.contains(child)) {
                nonParameters.add(child);
            }
        }
        this.nonParameterChildren = Collections.unmodifiableList(nonParameters);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Constructor<?> getConstructor() {
        return constructor;
    }

    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    public List<InternalNode> getConstructorParameterNodes() {
        return constructorParameterNodes;
    }

    /**
     * Returns the owning node's children, if any, that are not passed
     * to the constructor and must be populated after construction.
     */
    public List<InternalNode> getNonParameterChildren() {
        return nonParameterChildren;
    }
}
