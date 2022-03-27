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
package org.instancio.internal;

import org.instancio.internal.model.ArrayNode;
import org.instancio.internal.model.CollectionNode;
import org.instancio.internal.model.InternalModel;
import org.instancio.internal.model.MapNode;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.model.Node;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

class InstancioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioDriver.class);

    private final GeneratorFacade generatorFacade;
    private final Queue<CreateItem> queue = new ArrayDeque<>();
    private final ModelContext<?> context;
    private final Node rootNode;

    InstancioDriver(InternalModel<?> model) {
        this.context = model.getModelContext();
        this.rootNode = model.getRootNode();
        this.generatorFacade = new GeneratorFacade(context);
    }

    <T> T createEntryPoint() {
        final GeneratorResult rootResult = generatorFacade.generateNodeValue(rootNode, null);
        final Object value = rootResult.getValue();

        enqueueChildrenOf(rootNode, rootResult, queue);
        populateDataStructures(null, rootNode, rootResult);

        while (!queue.isEmpty()) {
            processNextItem(queue.poll());
        }

        //noinspection unchecked
        return (T) value;
    }

    private boolean shouldIgnored(final Node node) {
        return node.getField() == null
                || context.isIgnored(node.getField())
                || context.isIgnored(node.getKlass())
                || Modifier.isStatic(node.getField().getModifiers());
    }

    private void processNextItem(final CreateItem createItem) {
        LOG.trace("Creating: {}", createItem);

        final Node node = createItem.getNode();
        if (shouldIgnored(node)) {
            return;
        }

        final Field field = node.getField();
        final GeneratorResult generatorResult = generatorFacade.generateNodeValue(node, createItem.getOwner());

        if (generatorResult.getValue() == null) {
            if (!field.getType().isPrimitive()) {
                Verify.notNull(createItem.getOwner());
                ReflectionUtils.setField(createItem.getOwner(), field, null);
            }
            return;
        }

        ReflectionUtils.setField(createItem.getOwner(), field, generatorResult.getValue());

        if (generatorResult.ignoreChildren()) {
            // do not populate arrays/collections
            // or fields of objects created by user-supplied generators
            return;
        }

        enqueueChildrenOf(node, generatorResult, queue);

        populateDataStructures(createItem.getOwner(), node, generatorResult);
    }

    private void populateDataStructures(@Nullable Object owner, Node node, GeneratorResult generatorResult) {
        if (node instanceof CollectionNode) {
            populateCollection((CollectionNode) node, generatorResult, owner);
        } else if (node instanceof MapNode) {
            populateMap((MapNode) node, generatorResult, owner);
        } else if (node instanceof ArrayNode) {
            populateArray((ArrayNode) node, generatorResult, owner);
        }
    }

    private void populateArray(ArrayNode arrayNode, GeneratorResult generatorResult, Object arrayOwner) {
        final Object createdValue = generatorResult.getValue();
        final Node elementNode = arrayNode.getElementNode();

        // Field can be null when array is an element of a collection
        if (arrayNode.getField() != null) {
            ReflectionUtils.setField(arrayOwner, arrayNode.getField(), createdValue);
        }

        for (int i = 0, len = Array.getLength(createdValue); i < len; i++) {
            // nullable element
            if (generatorResult.getHints().nullableResult() && context.getRandomProvider().oneInTenTrue()) {
                continue;
            }

            final GeneratorResult elementResult = generatorFacade.generateNodeValue(elementNode, createdValue);
            final Object elementValue = elementResult.getValue();
            Array.set(createdValue, i, elementValue);

            enqueueChildrenOf(elementNode, elementResult, queue);
        }
    }

    private void populateCollection(CollectionNode collectionNode, GeneratorResult generatorResult, Object collectionOwner) {
        final Collection<Object> collectionInstance = (Collection<Object>) generatorResult.getValue();
        final Node elementNode = collectionNode.getElementNode();

        if (collectionNode.getField() != null)
            ReflectionUtils.setField(collectionOwner, collectionNode.getField(), collectionInstance);

        final boolean nullableElement = generatorResult.getHints().nullableElements();

        for (int i = 0; i < generatorResult.getHints().getDataStructureSize(); i++) {
            final GeneratorResult elementResult = generatorFacade.generateNodeValue(elementNode, collectionInstance);
            final Object elementValue;

            if (nullableElement && context.getRandomProvider().oneInTenTrue()) {
                elementValue = null;
            } else {
                elementValue = elementResult.getValue();
            }

            if (elementValue != null || nullableElement) {
                collectionInstance.add(elementValue);
            }

            // nested collection
            if (elementNode instanceof CollectionNode) {
                populateCollection((CollectionNode) elementNode, elementResult, collectionInstance);
            }

            enqueueChildrenOf(elementNode, elementResult, queue);
        }
    }

    // TODO refactor populate* methods to remove instanceof conditionals

    private void populateMap(MapNode mapNode, GeneratorResult generatorResult, Object mapOwner) {
        final Map<Object, Object> mapInstance = (Map<Object, Object>) generatorResult.getValue();
        final Node keyNode = mapNode.getKeyNode();
        final Node valueNode = mapNode.getValueNode();

        if (mapNode.getField() != null)
            ReflectionUtils.setField(mapOwner, mapNode.getField(), mapInstance);

        final boolean nullableKey = generatorResult.getHints().nullableKeys();
        final boolean nullableValue = generatorResult.getHints().nullableValues();

        for (int i = 0; i < generatorResult.getHints().getDataStructureSize(); i++) {
            final Object mapKey;


            if (nullableKey && context.getRandomProvider().oneInTenTrue()) {
                mapKey = null;
            } else {
                final GeneratorResult keyResult = generatorFacade.generateNodeValue(keyNode, mapInstance);
                enqueueChildrenOf(keyNode, keyResult, queue);
                mapKey = keyResult.getValue();
            }

            final Object mapValue;
            if (nullableValue && context.getRandomProvider().oneInTenTrue()) {
                mapValue = null;
            } else {
                final GeneratorResult valueResult = generatorFacade.generateNodeValue(valueNode, mapInstance);
                enqueueChildrenOf(valueNode, valueResult, queue);
                mapValue = valueResult.getValue();

                if (valueNode instanceof MapNode) {
                    populateMap((MapNode) valueNode, valueResult, mapInstance);
                } else if (valueNode instanceof CollectionNode) {
                    populateCollection((CollectionNode) valueNode, valueResult, mapInstance);
                } else if (valueNode instanceof ArrayNode) {
                    populateArray((ArrayNode) valueNode, valueResult, mapInstance);
                }
            }

            if ((mapKey != null || nullableKey) && (mapValue != null || nullableValue)) {
                mapInstance.put(mapKey, mapValue);
            }
        }
    }

    private static void enqueueChildrenOf(Node node, GeneratorResult result, Queue<CreateItem> queue) {
        if (!result.ignoreChildren()) {
            final Object owner = result.getValue();
            queue.addAll(node.getChildren().stream()
                    .map(it -> new CreateItem(it, owner))
                    .collect(toList()));
        }

    }
}
