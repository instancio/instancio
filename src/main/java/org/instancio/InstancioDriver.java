package org.instancio;

import org.instancio.model.ArrayNode;
import org.instancio.model.CollectionNode;
import org.instancio.model.InternalModel;
import org.instancio.model.MapNode;
import org.instancio.model.ModelContext;
import org.instancio.model.Node;
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

    // TODO
    private static final int ARRAY_SIZE = 2;
    private static final int MAP_SIZE = 2;
    private static final int COLLECTION_SIZE = 2;

    private final GeneratorFacade generatorFacade;
    private final Queue<CreateItem> queue = new ArrayDeque<>();
    private final ModelContext context;
    private final Node rootNode;

    public InstancioDriver(InternalModel<?> model) {
        this.context = model.getModelContext();
        this.rootNode = model.getRootNode();
        this.generatorFacade = new GeneratorFacade(context);
    }

    <T> T createEntryPoint() {
        final GeneratorResult<?> rootResult = generatorFacade.generateNodeValue(rootNode, null);
        final Object value = rootResult.getValue();

        enqueueChildrenOf(rootNode, rootResult, queue);
        populateDataStructures(null, rootNode, value);

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
                || context.getIgnoredClasses().contains(node.getEffectiveType().getRawType())
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

        populateDataStructures(createItem.getOwner(), node, createdValue);
    }

    private void populateDataStructures(@Nullable Object owner, Node node, Object createdValue) {
        if (node instanceof CollectionNode) {
            populateCollection((CollectionNode) node, (Collection<Object>) createdValue, owner);
        } else if (node instanceof MapNode) {
            final Map<Object, Object> mapInstance = (Map<Object, Object>) createdValue;
            populateMap((MapNode) node, mapInstance, owner);
        } else if (node instanceof ArrayNode) {
            populateArray((ArrayNode) node, createdValue, owner);
        }
    }

    private void populateArray(ArrayNode arrayNode, Object createdValue, Object arrayOwner) {
        final Node elementNode = arrayNode.getElementNode();

        // Field can be null when array is an element of a collection
        if (arrayNode.getField() != null) {
            ReflectionUtils.setField(arrayOwner, arrayNode.getField(), createdValue);
        }

        for (int i = 0; i < ARRAY_SIZE; i++) {
            final GeneratorResult<?> generatorResult = generatorFacade.generateNodeValue(elementNode, createdValue);
            final Object elementValue = generatorResult.getValue();
            Array.set(createdValue, i, elementValue);

            enqueueChildrenOf(elementNode, generatorResult, queue);
        }
    }

    private void populateCollection(CollectionNode collectionNode, Collection<Object> collectionInstance, Object collectionOwner) {

        final Node elementNode = collectionNode.getElementNode();

        if (collectionNode.getField() != null)
            ReflectionUtils.setField(collectionOwner, collectionNode.getField(), collectionInstance);

        for (int i = 0; i < COLLECTION_SIZE; i++) {
            final GeneratorResult<?> generatorResult = generatorFacade.generateNodeValue(elementNode, collectionInstance);
            final Object elementValue = generatorResult.getValue();
            if (elementValue != null) {
                collectionInstance.add(elementValue);
            }

            // nested list
            if (elementNode instanceof CollectionNode) {
                populateCollection((CollectionNode) elementNode, (Collection<Object>) elementValue, collectionInstance);
            }

            enqueueChildrenOf(elementNode, generatorResult, queue);

        }
    }

    // TODO refactor populate* methods to remove instanceof conditionals

    private void populateMap(MapNode mapNode, Map<Object, Object> mapInstance, Object mapOwner) {
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


        for (int i = 0; i < MAP_SIZE; i++) {
            final GeneratorResult<?> generatorKeyResult = generatorFacade.generateNodeValue(keyNode, mapInstance);
            final GeneratorResult<?> generatorValueResult = generatorFacade.generateNodeValue(valueNode, mapInstance);

            final Object mapKey = generatorKeyResult.getValue();
            final Object mapValue = generatorValueResult.getValue();

            if (mapKey != null) {
                mapInstance.put(mapKey, mapValue);

                enqueueChildrenOf(keyNode, generatorKeyResult, queue);
                enqueueChildrenOf(valueNode, generatorValueResult, queue);

                if (valueNode instanceof MapNode) {
                    populateMap((MapNode) valueNode, (Map<Object, Object>) mapValue, mapInstance);
                } else if (valueNode instanceof CollectionNode) {
                    populateCollection((CollectionNode) valueNode, (Collection<Object>) mapValue, mapInstance);
                } else if (valueNode instanceof ArrayNode) {
                    populateArray((ArrayNode) valueNode, mapValue, mapInstance);
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
