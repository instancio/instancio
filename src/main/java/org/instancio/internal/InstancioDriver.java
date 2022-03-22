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
        final GeneratorResult<?> rootResult = generatorFacade.generateNodeValue(rootNode, null);
        final Object value = rootResult.getValue();

        enqueueChildrenOf(rootNode, rootResult, queue);
        populateDataStructures(null, rootNode, rootResult);

        while (!queue.isEmpty()) {
            processNestItem(queue.poll());
        }

        //noinspection unchecked
        return (T) value;
    }

    private void processNestItem(final CreateItem createItem) {
        LOG.debug("Creating: {}", createItem);

        final Node node = createItem.getNode();
        final Field field = node.getField();

        if (field == null
                || context.getIgnoredFields().contains(field)
                || context.getIgnoredClasses().contains(node.getKlass())
                || Modifier.isStatic(field.getModifiers())) {
            return;
        }

        final GeneratorResult<?> generatorResult = generatorFacade.generateNodeValue(node, createItem.getOwner());
        final Object createdValue = generatorResult.getValue();

        if (createdValue == null) {
            Verify.notNull(createItem.getOwner());
            ReflectionUtils.setField(createItem.getOwner(), field, null);
            return;
        }

        ReflectionUtils.setField(createItem.getOwner(), field, createdValue);

        if (generatorResult.ignoreChildren()) {
            // do not populate arrays/collections
            // or fields of objects created by user-supplied generators
            return;
        }

        enqueueChildrenOf(node, generatorResult, queue);

        populateDataStructures(createItem.getOwner(), node, generatorResult);
    }

    private void populateDataStructures(@Nullable Object owner, Node node, GeneratorResult<?> generatorResult) {
        if (node instanceof CollectionNode) {
            populateCollection((CollectionNode) node, generatorResult, owner);
        } else if (node instanceof MapNode) {
            populateMap((MapNode) node, generatorResult, owner);
        } else if (node instanceof ArrayNode) {
            populateArray((ArrayNode) node, generatorResult, owner);
        }
    }

    private void populateArray(ArrayNode arrayNode, GeneratorResult<?> generatorResult, Object arrayOwner) {
        final Object createdValue = generatorResult.getValue();
        final Node elementNode = arrayNode.getElementNode();

        // Field can be null when array is an element of a collection
        if (arrayNode.getField() != null) {
            ReflectionUtils.setField(arrayOwner, arrayNode.getField(), createdValue);
        }

        for (int i = 0, len = Array.getLength(createdValue); i < len; i++) {
            final GeneratorResult<?> elementResult = generatorFacade.generateNodeValue(elementNode, createdValue);
            final Object elementValue = elementResult.getValue();
            Array.set(createdValue, i, elementValue);

            enqueueChildrenOf(elementNode, elementResult, queue);
        }
    }

    private void populateCollection(CollectionNode collectionNode, GeneratorResult<?> generatorResult, Object collectionOwner) {
        final Collection<Object> collectionInstance = (Collection<Object>) generatorResult.getValue();
        final Node elementNode = collectionNode.getElementNode();

        if (collectionNode.getField() != null)
            ReflectionUtils.setField(collectionOwner, collectionNode.getField(), collectionInstance);

        for (int i = 0; i < generatorResult.getSettings().getDataStructureSize(); i++) {
            final GeneratorResult<?> elementResult = generatorFacade.generateNodeValue(elementNode, collectionInstance);
            final Object elementValue = elementResult.getValue();
            if (elementValue != null) {
                collectionInstance.add(elementValue);
            }

            // nested list
            if (elementNode instanceof CollectionNode) {
                populateCollection((CollectionNode) elementNode, elementResult, collectionInstance);
            }

            enqueueChildrenOf(elementNode, elementResult, queue);

        }
    }

    // TODO refactor populate* methods to remove instanceof conditionals

    private void populateMap(MapNode mapNode, GeneratorResult<?> generatorResult, Object mapOwner) {
        final Map<Object, Object> mapInstance = (Map<Object, Object>) generatorResult.getValue();
        final Node keyNode = mapNode.getKeyNode();
        final Node valueNode = mapNode.getValueNode();

        if (mapNode.getField() != null)
            ReflectionUtils.setField(mapOwner, mapNode.getField(), mapInstance);

        // How to collect children when map's 'value' is another map?
        // (NOTE this probably applies to nested Collection and Array nodes too)
        //
        // 'value' is a MapNode, therefore its 'getChildren()' would return an empty list
        // option1: have MapNode.children return key/value nodes' children. don't add children of key/value node directly
        //          Risk: processing duplicate children if they all end up the queue
        // option2: check if value node is a Map/Collection/Array node... then get element/key/value nodes children (UGLY)


        for (int i = 0; i < generatorResult.getSettings().getDataStructureSize(); i++) {
            final GeneratorResult<?> keyResult = generatorFacade.generateNodeValue(keyNode, mapInstance);
            final GeneratorResult<?> valueResult = generatorFacade.generateNodeValue(valueNode, mapInstance);

            final Object mapKey = keyResult.getValue();
            final Object mapValue = valueResult.getValue();

            if (mapKey != null) {
                mapInstance.put(mapKey, mapValue);

                enqueueChildrenOf(keyNode, keyResult, queue);
                enqueueChildrenOf(valueNode, valueResult, queue);

                if (valueNode instanceof MapNode) {
                    populateMap((MapNode) valueNode, valueResult, mapInstance);
                } else if (valueNode instanceof CollectionNode) {
                    populateCollection((CollectionNode) valueNode, valueResult, mapInstance);
                } else if (valueNode instanceof ArrayNode) {
                    populateArray((ArrayNode) valueNode, valueResult, mapInstance);
                }
            }
        }
    }

    private static void enqueueChildrenOf(Node node, GeneratorResult<?> result, Queue<CreateItem> queue) {
        if (!result.ignoreChildren()) {
            final Object owner = result.getValue();
            queue.addAll(node.getChildren().stream()
                    .map(it -> new CreateItem(it, owner))
                    .collect(toList()));
        }

    }
}
