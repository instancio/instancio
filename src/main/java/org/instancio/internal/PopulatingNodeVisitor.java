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
package org.instancio.internal;

import org.instancio.internal.nodes.ArrayNode;
import org.instancio.internal.nodes.ClassNode;
import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.nodes.MapNode;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeVisitor;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

public class PopulatingNodeVisitor implements NodeVisitor {

    private final Object owner;
    private final GeneratorResult generatorResult;
    private final GeneratorFacade generatorFacade;
    private final ModelContext<?> context;
    private final Queue<CreateItem> queue;
    private final CallbackHandler callbackHandler;

    public PopulatingNodeVisitor(@Nullable final Object owner,
                                 final GeneratorResult generatorResult,
                                 final GeneratorFacade generatorFacade,
                                 final ModelContext<?> context,
                                 final Queue<CreateItem> queue,
                                 final CallbackHandler callbackHandler) {
        this.owner = owner;
        this.generatorResult = generatorResult;
        this.generatorFacade = generatorFacade;
        this.context = context;
        this.queue = queue;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void visitClassNode(final ClassNode node) {
        final Field field = node.getField();
        if (field == null) {
            if (owner == null) { // i.e. root node
                enqueueChildrenOf(node, generatorResult, queue);
            }
            return;
        }

        Verify.notNull(owner, "null owner for node: %s", node);

        if (generatorResult.getValue() != null) {
            ReflectionUtils.setField(owner, field, generatorResult.getValue());
            enqueueChildrenOf(node, generatorResult, queue);
        } else if (!field.getType().isPrimitive()) {
            ReflectionUtils.setField(owner, field, null);
        }
    }

    @Override
    public void visitCollectionNode(final CollectionNode collectionNode) {
        if (generatorResult.getValue() == null) {
            return;
        }

        final Collection<Object> collectionObj = (Collection<Object>) generatorResult.getValue();
        final Node elementNode = collectionNode.getElementNode();

        if (collectionNode.getField() != null) {
            ReflectionUtils.setField(owner, collectionNode.getField(), collectionObj);
        }

        final boolean nullableElement = generatorResult.getHints().nullableElements();

        for (int i = 0; i < generatorResult.getHints().getDataStructureSize(); i++) {
            final GeneratorResult elementResult = generatorFacade.generateNodeValue(elementNode, collectionObj);
            final Object elementValue;

            if (context.getRandomProvider().diceRoll(nullableElement)) {
                elementValue = null;
            } else {
                elementValue = elementResult.getValue();
                elementNode.accept(new PopulatingNodeVisitor(collectionObj, elementResult, generatorFacade, context, queue, callbackHandler));
                enqueueChildrenOf(elementNode, elementResult, queue);
            }

            if (elementValue != null || nullableElement) {
                collectionObj.add(elementValue);
                callbackHandler.addResult(elementNode, elementResult);
            }
        }
    }

    @Override
    public void visitMapNode(final MapNode mapNode) {
        if (generatorResult.getValue() == null) {
            return;
        }

        final Map<Object, Object> mapObj = (Map<Object, Object>) generatorResult.getValue();
        final Node keyNode = mapNode.getKeyNode();
        final Node valueNode = mapNode.getValueNode();

        if (mapNode.getField() != null) {
            ReflectionUtils.setField(owner, mapNode.getField(), mapObj);
        }

        final boolean nullableKey = generatorResult.getHints().nullableMapKeys();
        final boolean nullableValue = generatorResult.getHints().nullableMapValues();

        for (int i = 0; i < generatorResult.getHints().getDataStructureSize(); i++) {
            final Object mapKey;

            GeneratorResult keyResult = null;
            GeneratorResult valueResult = null;

            if (context.getRandomProvider().diceRoll(nullableKey)) {
                mapKey = null;
            } else {
                keyResult = generatorFacade.generateNodeValue(keyNode, mapObj);
                enqueueChildrenOf(keyNode, keyResult, queue);
                mapKey = keyResult.getValue();
            }

            final Object mapValue;
            if (context.getRandomProvider().diceRoll(nullableValue)) {
                mapValue = null;
            } else {
                valueResult = generatorFacade.generateNodeValue(valueNode, mapObj);
                enqueueChildrenOf(valueNode, valueResult, queue);
                mapValue = valueResult.getValue();
                valueNode.accept(new PopulatingNodeVisitor(mapObj, valueResult, generatorFacade, context, queue, callbackHandler));
            }

            if ((mapKey != null || nullableKey) && (mapValue != null || nullableValue)) {
                mapObj.put(mapKey, mapValue);
                if (mapKey != null) {
                    callbackHandler.addResult(keyNode, keyResult);
                }
                if (mapValue != null) {
                    callbackHandler.addResult(valueNode, valueResult);
                }
            }
        }
    }

    @Override
    public void visitArrayNode(final ArrayNode arrayNode) {
        if (generatorResult.getValue() == null) {
            return;
        }

        final Object arrayObj = generatorResult.getValue();
        final Node elementNode = arrayNode.getElementNode();

        // Field can be null when array is an element of a collection
        if (arrayNode.getField() != null) {
            ReflectionUtils.setField(owner, arrayNode.getField(), arrayObj);
        }

        for (int i = 0, len = Array.getLength(arrayObj); i < len; i++) {
            final boolean isNullableElement = generatorResult.getHints().nullableElements();
            if (context.getRandomProvider().diceRoll(isNullableElement)) {
                continue;
            }

            final GeneratorResult elementResult = generatorFacade.generateNodeValue(elementNode, arrayObj);
            final Object elementValue = elementResult.getValue();
            Array.set(arrayObj, i, elementValue);
            enqueueChildrenOf(elementNode, elementResult, queue);
            callbackHandler.addResult(elementNode, elementResult);
        }
    }

    private static void enqueueChildrenOf(final Node node, final GeneratorResult result, final Queue<CreateItem> queue) {
        if (!result.ignoreChildren()) {
            final Object owner = result.getValue();
            queue.addAll(node.getChildren().stream()
                    .map(it -> new CreateItem(it, owner))
                    .collect(toList()));
        }
    }
}
