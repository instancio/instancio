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
import static org.instancio.jpa.util.JpaMetamodelUtil.setAttributeValue;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.PluralAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EntityGraphAssociationFixer {

    private final Metamodel metamodel;

    public EntityGraphAssociationFixer(Metamodel metamodel) {
        this.metamodel = metamodel;
    }

    public void fixAssociations(Object entity) {
        Set<Object> visited = new HashSet<>();
        fixAssociations0(entity, visited);
    }

    private void fixAssociations0(Object entity, Set<Object> visited) {
        if (visited.contains(entity)) {
            return;
        }
        visited.add(entity);
        EntityType<?> entityType = metamodel.entity(entity.getClass());
        entityType.getAttributes().stream()
            .filter(Attribute::isAssociation)
            .forEach(attr -> {
                if (attr.getPersistentAttributeType() == MANY_TO_ONE) {
                    // we need to add the "entity" to the corresponding OneToMany
                    fixManyToOneAssociation(entity, attr);
                } else if (attr.getPersistentAttributeType() == ONE_TO_ONE) {
                    // we need to point the other association side to "entity"
                    fixOneToOneAssociation(entity, attr);
                } else if (attr.getPersistentAttributeType() == ONE_TO_MANY) {
                    // we need to point other association side to "entity"
                    fixOneToManyAssociation(entity, (PluralAttribute<?, ?, ?>) attr);
                } else if (attr.getPersistentAttributeType() == MANY_TO_MANY) {
                    // we need to add the "entity" to all the corresponding ManyToMany
                    fixManyToManyAssociation(entity, (PluralAttribute<?, ?, ?>) attr);
                } else {
                    throw new IllegalStateException();
                }
                Object attributeValue = resolveAttributeValue(entity, attr);
                if (attributeValue != null) {
                    if (attributeValue instanceof Collection<?>) {
                        ((Collection<?>) attributeValue).forEach(collectionElement -> fixAssociations0(collectionElement, visited));
                    } else {
                        fixAssociations0(attributeValue, visited);
                    }
                }
            });
        visited.remove(entity);
    }

    private <X, Y> void fixManyToOneAssociation(Object associationStartValue, Attribute<X, Y> associationStart) {
        Object associationEndValue = resolveAttributeValue(associationStartValue, associationStart);
        if (associationEndValue != null) {
            EntityType<Y> associationEndType = metamodel.entity(associationStart.getJavaType());
            findOneToManyWithMappedBy(associationEndType, associationStart.getName())
                .ifPresent(associationEnd -> {
                    populateCollectionOrMap(associationEndValue, associationEnd, associationStartValue);
                });
        }
    }

    private <X, Y> void fixOneToOneAssociation(Object associationStartValue, Attribute<X, Y> associationStart) {
        Object associationEndValue = resolveAttributeValue(associationStartValue, associationStart);
        if (associationEndValue != null) {
            String mappedByOnStartSide = resolveMappedBy(associationStart.getJavaMember());
            EntityType<Y> associationEndType =
                metamodel.entity(associationStart.getJavaType());
            if (mappedByOnStartSide != null) {
                findOneToOneWithAttributeName(associationEndType, mappedByOnStartSide)
                    .ifPresent(associationEnd ->
                        setAttributeValue(associationEndValue, associationEnd, associationStartValue));
            } else {
                findOneToOneWithMappedBy(associationEndType, associationStart.getName())
                    .ifPresent(associationEnd ->
                        setAttributeValue(associationEndValue, associationEnd, associationStartValue));
            }
        }
    }

    private <X, Y, E> void fixOneToManyAssociation(Object associationStartValue, PluralAttribute<X, Y, E> associationStart) {
        if (associationStart.getCollectionType() == PluralAttribute.CollectionType.MAP) {
            throw new UnsupportedOperationException();
        }
        Collection<?> associationEndCollectionValue = (Collection<?>) resolveAttributeValue(associationStartValue, associationStart);
        if (associationEndCollectionValue != null) {
            String mappedByOnStartSide = resolveMappedBy(associationStart.getJavaMember());
            if (mappedByOnStartSide != null) {
                EntityType<E> associationEndType = metamodel.entity(associationStart.getElementType().getJavaType());
                findManyToOneWithAttributeName(associationEndType, mappedByOnStartSide)
                    .ifPresent(associationEnd ->
                        associationEndCollectionValue.forEach(associationEndElementValue ->
                            setAttributeValue(associationEndElementValue, associationEnd, associationStartValue)));
            }
        }
    }

    private <X, Y, E> void fixManyToManyAssociation(Object associationStartValue, PluralAttribute<X, Y, E> associationStart) {
        if (associationStart.getCollectionType() == PluralAttribute.CollectionType.MAP) {
            throw new UnsupportedOperationException();
        }
        Collection<?> associationEndCollectionValue = (Collection<?>) resolveAttributeValue(associationStartValue, associationStart);
        if (associationEndCollectionValue != null) {
            String mappedByOnStartSide = resolveMappedBy(associationStart.getJavaMember());
            EntityType<E> associationEndType = metamodel.entity(associationStart.getElementType().getJavaType());
            if (mappedByOnStartSide != null) {
                findManyToManyWithAttributeName(associationEndType, mappedByOnStartSide)
                    .ifPresent(associationEnd ->
                        associationEndCollectionValue.forEach(associationEndElementValue ->
                            populateCollectionOrMap(associationEndElementValue, associationEnd, associationStartValue)));

            } else {
                findManyToManyWithMappedBy(associationEndType, associationStart.getName())
                    .ifPresent(associationEnd ->
                        associationEndCollectionValue.forEach(associationEndElementValue ->
                            populateCollectionOrMap(associationEndElementValue, associationEnd, associationStartValue)));
            }
        }
    }

    private static Collection<?> initializeCollection(Object entity, PluralAttribute<?, ?, ?> attribute) {
        Collection<?> newCollection;
        switch (attribute.getCollectionType()) {
            case SET: newCollection = new HashSet<>(); break;
            case LIST:
            case COLLECTION: newCollection = new ArrayList<>(); break;
            case MAP: throw new IllegalStateException();
            default: throw new IllegalStateException();
        }
        setAttributeValue(entity, attribute, newCollection);
        return newCollection;
    }

    private static void populateCollectionOrMap(
        Object entity, PluralAttribute<?, ?, ?> attribute, Object newElement
    ) {
        if (attribute.getCollectionType() == PluralAttribute.CollectionType.MAP) {
            throw new UnsupportedOperationException();
        } else {
            Collection<Object> reverseAssociationStartValue
                = (Collection<Object>) resolveAttributeValue(entity, attribute);
            if (reverseAssociationStartValue == null) {
                reverseAssociationStartValue = (Collection<Object>) initializeCollection(entity, attribute);
            }
            reverseAssociationStartValue.add(newElement);
        }
    }

    private static <T> Optional<PluralAttribute<? super T, ?, ?>> findOneToManyWithMappedBy(
        ManagedType<T> managedType, String mappedBy
    ) {
        return managedType.getPluralAttributes().stream()
            .filter(attr -> attr.getPersistentAttributeType() == ONE_TO_MANY)
            .filter(attr -> mappedBy.equals(resolveMappedBy(attr.getJavaMember())))
            .findAny();
    }

    private static <T> Optional<Attribute<? super T, ?>> findOneToOneWithMappedBy(
        ManagedType<T> managedType, String mappedBy
    ) {
        return managedType.getAttributes().stream()
            .filter(attr -> attr.getPersistentAttributeType() == ONE_TO_ONE)
            .filter(attr -> mappedBy.equals(resolveMappedBy(attr.getJavaMember())))
            .findAny();
    }

    private static <T> Optional<Attribute<? super T, ?>> findOneToOneWithAttributeName(
        ManagedType<T> managedType, String attributeName
    ) {
        return managedType.getAttributes().stream()
            .filter(attr -> attr.getPersistentAttributeType() == ONE_TO_ONE)
            .filter(attr -> attributeName.equals(attr.getName()))
            .findAny();
    }

    private static <T> Optional<Attribute<? super T, ?>> findManyToOneWithAttributeName(
        ManagedType<T> managedType, String attributeName
    ) {
        return managedType.getAttributes().stream()
            .filter(attr -> attr.getPersistentAttributeType() == MANY_TO_ONE)
            .filter(attr -> attributeName.equals(attr.getName()))
            .findAny();
    }

    private static <T> Optional<PluralAttribute<? super T, ?, ?>> findManyToManyWithMappedBy(
        ManagedType<T> managedType, String mappedBy
    ) {
        return managedType.getPluralAttributes().stream()
            .filter(attr -> attr.getPersistentAttributeType() == MANY_TO_MANY)
            .filter(attr -> mappedBy.equals(resolveMappedBy(attr.getJavaMember())))
            .findAny();
    }

    private static <T> Optional<PluralAttribute<? super T, ?, ?>> findManyToManyWithAttributeName(
        ManagedType<T> managedType, String attributeName
    ) {
        return managedType.getPluralAttributes().stream()
            .filter(attr -> attr.getPersistentAttributeType() == MANY_TO_MANY)
            .filter(attr -> attributeName.equals(attr.getName()))
            .findAny();
    }
}
