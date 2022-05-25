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

import org.instancio.generator.GeneratorResult;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.ArrayNode;
import org.instancio.internal.nodes.ClassNode;
import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.nodes.MapNode;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeVisitor;
import org.instancio.util.ArrayUtils;
import org.instancio.util.CollectionUtils;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static java.util.stream.Collectors.toList;
import static org.instancio.util.ExceptionHandler.conditionalFailOnError;

public class PopulatingNodeVisitor implements NodeVisitor {
    private static final Object NULL_VALUE = null;
    private final Object owner;
    private final GeneratorResult generatorResult;
    private final ModelContext<?> context;
    private final Queue<CreateItem> queue;
    private final InstancioEngine engine;

    public PopulatingNodeVisitor(@Nullable final Object owner,
                                 final GeneratorResult generatorResult,
                                 final ModelContext<?> context,
                                 final Queue<CreateItem> queue,
                                 final InstancioEngine engine) {
        this.owner = owner;
        this.generatorResult = generatorResult;
        this.context = context;
        this.queue = queue;
        this.engine = engine;
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
            conditionalFailOnError(() -> ReflectionUtils.setField(owner, field, generatorResult.getValue()));
            enqueueChildrenOf(node, generatorResult, queue);
        } else if (!field.getType().isPrimitive()) {
            conditionalFailOnError(() -> ReflectionUtils.setField(owner, field, NULL_VALUE));
        }
    }

    @Override
    public void visitCollectionNode(final CollectionNode collectionNode) {
        if (generatorResult.getValue() == null) {
            return;
        }

        final Collection<Object> collection = (Collection<Object>) generatorResult.getValue();
        final Node elementNode = collectionNode.getOnlyChild();

        if (collectionNode.getField() != null) {
            conditionalFailOnError(() -> ReflectionUtils.setField(owner, collectionNode.getField(), collection));
        }

        final boolean nullableElement = generatorResult.getHints().nullableElements();

        for (int i = 0; i < generatorResult.getHints().getDataStructureSize(); i++) {
            addCollectionValue(collection, elementNode, nullableElement);
        }

        if (!generatorResult.getHints().getWithElements().isEmpty()) {
            collection.addAll(generatorResult.getHints().getWithElements());
            CollectionUtils.shuffle(collection, context.getRandom());
        }
    }

    private void addCollectionValue(final Collection<Object> collection, final Node elementNode, final boolean nullableElement) {
        final Object elementValue;

        if (context.getRandom().diceRoll(nullableElement)) {
            elementValue = null;
            engine.notifyListeners(elementNode, null);
        } else {
            final Optional<GeneratorResult> optResult = engine.createObject(elementNode, collection);

            if (!optResult.isPresent()) {
                return;
            }

            final GeneratorResult elementResult = optResult.get();
            elementValue = optResult.get().getValue();
            enqueueChildrenOf(elementNode, elementResult, queue);
        }

        if (elementValue != null || nullableElement) {
            collection.add(elementValue);
        }
    }

    @Override
    public void visitMapNode(final MapNode mapNode) {
        if (generatorResult.getValue() == null) {
            return;
        }

        final Map<Object, Object> mapObj = (Map<Object, Object>) generatorResult.getValue();
        final Node keyNode = mapNode.getChildren().get(0);
        final Node valueNode = mapNode.getChildren().get(1);

        if (mapNode.getField() != null) {
            ReflectionUtils.setField(owner, mapNode.getField(), mapObj);
        }

        final boolean nullableKey = generatorResult.getHints().nullableMapKeys();
        final boolean nullableValue = generatorResult.getHints().nullableMapValues();

        for (int i = 0; i < generatorResult.getHints().getDataStructureSize(); i++) {
            putKeyValue(mapObj, keyNode, valueNode, nullableKey, nullableValue);
        }
    }

    private void putKeyValue(final Map<Object, Object> map,
                             final Node keyNode,
                             final Node valueNode,
                             final boolean nullableKey,
                             final boolean nullableValue) {
        final Object mapKey;

        GeneratorResult keyResult;
        GeneratorResult valueResult;

        if (context.getRandom().diceRoll(nullableKey)) {
            mapKey = null;
            engine.notifyListeners(keyNode, null);
        } else {
            final Optional<GeneratorResult> keyResultOpt = engine.createObject(keyNode, map);
            if (keyResultOpt.isPresent()) {
                keyResult = keyResultOpt.get();
                enqueueChildrenOf(keyNode, keyResult, queue);
                mapKey = keyResult.getValue();
            } else {
                mapKey = null;
            }
        }

        final Object mapValue;
        if (context.getRandom().diceRoll(nullableValue)) {
            mapValue = null;
            engine.notifyListeners(valueNode, null);
        } else {
            final Optional<GeneratorResult> valueResultOpt = engine.createObject(valueNode, map);
            if (valueResultOpt.isPresent()) {
                valueResult = valueResultOpt.get();
                enqueueChildrenOf(valueNode, valueResult, queue);
                mapValue = valueResult.getValue();
            } else {
                mapValue = null;
            }
        }

        if ((mapKey != null || nullableKey) && (mapValue != null || nullableValue)) {
            map.put(mapKey, mapValue);
        }
    }

    @Override
    public void visitArrayNode(final ArrayNode arrayNode) {
        if (generatorResult.getValue() == null) {
            return;
        }

        final Object arrayObj = generatorResult.getValue();
        final Node elementNode = arrayNode.getOnlyChild();

        // Field can be null when array is an element of a collection
        if (arrayNode.getField() != null) {
            ReflectionUtils.setField(owner, arrayNode.getField(), arrayObj);
        }

        final List<?> withElements = generatorResult.getHints().getWithElements();

        int index = 0;
        for (int len = Array.getLength(arrayObj) - withElements.size(); index < len; index++) {
            final boolean isNullableElement = generatorResult.getHints().nullableElements();
            if (context.getRandom().diceRoll(isNullableElement)) {
                engine.notifyListeners(elementNode, null);
                continue;
            }

            final Optional<GeneratorResult> optResult = engine.createObject(elementNode, arrayObj);
            if (optResult.isPresent()) {
                final GeneratorResult elementResult = optResult.get();
                final Object elementValue = elementResult.getValue();
                Array.set(arrayObj, index, elementValue);
                enqueueChildrenOf(elementNode, elementResult, queue);
            }
        }

        if (!withElements.isEmpty()) {
            for (int j = 0; j < withElements.size(); j++) {
                Array.set(arrayObj, j + index, withElements.get(j));
            }
            ArrayUtils.shuffle(arrayObj, context.getRandom());
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
