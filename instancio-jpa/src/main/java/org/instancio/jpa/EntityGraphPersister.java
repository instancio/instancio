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

import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_MANY;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_ONE;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_MANY;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_ONE;
import static org.instancio.jpa.util.JpaMetamodelUtil.resolveAttributeValue;
import static org.instancio.jpa.util.JpaMetamodelUtil.resolveMappedBy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EntityGraphPersister {

    private final EntityManager entityManager;
    private final Metamodel metamodel;

    public EntityGraphPersister(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.metamodel = entityManager.getMetamodel();
    }

    public void persist(Object entity) {
        List<Object> visited = new ArrayList<>();
        persist0(entity, visited);
    }

    private void persist0(Object entity, List<Object> visited) {
        if (entityManager.contains(entity)) {
            return;
        }
        if (visited.contains(entity)) {
            String cycle = visited.stream()
                .map(obj -> String.format("%s@%s", obj.getClass().getName(), System.identityHashCode(obj)))
                .collect(Collectors.joining(" -> "));
            cycle += " -> " + String.format("%s@%s", entity.getClass().getName(), System.identityHashCode(entity));
            throw new IllegalStateException("Cycle detected: " + cycle);
        }
        visited.add(entity);
        EntityType<?> entityType = metamodel.entity(entity.getClass());
        entityType.getSingularAttributes().forEach(attr -> {
            if (attr.getPersistentAttributeType() == MANY_TO_ONE
                || attr.getPersistentAttributeType() == ONE_TO_ONE && !isOwnedSide(attr.getJavaMember())
            ) {
                Object attrValue = resolveAttributeValue(entity, attr);
                if (attrValue != null) {
                    persist0(attrValue, visited);
                }
            }
        });
        entityManager.persist(entity);
        entityType.getAttributes().forEach(attr -> {
            if (attr.getPersistentAttributeType() == ONE_TO_ONE && isOwnedSide(attr.getJavaMember())
            ) {
                Object attrValue = resolveAttributeValue(entity, attr);
                if (attrValue != null && !visited.contains(attrValue)) {
                    persist0(attrValue, visited);
                }
            } else if (attr.getPersistentAttributeType() == ONE_TO_MANY
                || attr.getPersistentAttributeType() == MANY_TO_MANY
            ) {
                Collection<?> collection = (Collection<?>) resolveAttributeValue(entity, attr);
                if (collection != null) {
                    collection.stream()
                        .filter(element -> !visited.contains(element))
                        .forEach(element -> persist0(element, visited));
                }
            }
        });
        visited.remove(entity);
    }

    private static boolean isOwnedSide(Member member) {
        return resolveMappedBy(member) != null;
    }
}
