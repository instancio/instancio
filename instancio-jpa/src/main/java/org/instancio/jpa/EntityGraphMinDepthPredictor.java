/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.jpa;

import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.HashSet;
import java.util.Set;

public class EntityGraphMinDepthPredictor {

    private final Metamodel metamodel;

    public EntityGraphMinDepthPredictor(Metamodel metamodel) {
        this.metamodel = metamodel;
    }

    public int predictRequiredDepth(Class<?> entityClass) {
        Set<Class<?>> visited = new HashSet<>();
        return predictRequiredMaxDepth0(entityClass, visited);
    }

    private int predictRequiredMaxDepth0(Class<?> entityClass, Set<Class<?>> visited) {
        if (visited.contains(entityClass)) {
            return 0;
        }
        visited.add(entityClass);

        ManagedType<?> managedType = metamodel.managedType(entityClass);
        int maxDepth = managedType.getAttributes().stream()
            .filter(attr -> attr instanceof SingularAttribute && !((SingularAttribute<?, ?>) attr).isOptional())
            .mapToInt(attr -> {
                int depth;
                switch (attr.getPersistentAttributeType()) {
                    case ONE_TO_ONE:
                    case MANY_TO_ONE: depth = 1 + predictRequiredMaxDepth0(attr.getJavaType(), visited); break;
                    case EMBEDDED: depth = 1 + predictRequiredMaxDepth0(attr.getJavaType(), visited); break; // TODO: what about attribute overrides?
                    case BASIC: depth = 1; break;
                    default: depth = 0;
                }
                return depth;
            }).max().orElse(0);

        visited.remove(entityClass);
        return maxDepth;
    }
}
