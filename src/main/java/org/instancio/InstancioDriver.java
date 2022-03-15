package org.instancio;

import org.instancio.model.ArrayNode;
import org.instancio.model.ClassNode;
import org.instancio.model.CollectionNode;
import org.instancio.model.MapNode;
import org.instancio.model.Node;
import org.instancio.model.NodeContext;
import org.instancio.model.NodeFactory;
import org.instancio.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

class InstancioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioDriver.class);

    private final InstancioContext context;
    private final GeneratorFacade generatorFacade;
    private final Queue<CreateItem> queue = new ArrayDeque<>();

    public InstancioDriver(InstancioContext context) {
        this.context = context;
        this.generatorFacade = new GeneratorFacade(context);
    }

    <C> C createClassEntryPoint(Class<C> rootClass) {
        final NodeFactory nodeFactory = new NodeFactory();
        final Node rootNode = nodeFactory.createNode(
                new NodeContext(context.getRootTypeMap()), rootClass, null, null, null);

        final GeneratorResult<C> rootResult = generatorFacade.generateNodeValue(rootNode, null);

        enqueueChildrenOf(rootNode, rootResult, queue);

        while (!queue.isEmpty()) {
            processNestItem(queue.poll());
        }

        return rootResult.getValue();
    }

    private void processNestItem(final CreateItem createItem) {
        LOG.debug("Creating: {}", createItem);

        final Node node = createItem.getNode();
        final Field field = node.getField();

        if (field == null || context.isExcluded(field) || Modifier.isStatic(field.getModifiers())) {
            return;
        }

        final GeneratorResult<?> generatorResult = generatorFacade.generateNodeValue(node, createItem.getOwner());
        final Object createdValue = generatorResult.getValue();

        if (createdValue == null) {
            return;
        }

        enqueueChildrenOf(node, generatorResult, queue);

        if (node instanceof ClassNode) {
            ReflectionUtils.setField(createItem.getOwner(), field, createdValue);
        } else if (node instanceof CollectionNode) {
            final Object collectionOwner = createItem.getOwner();
            populateCollection((CollectionNode) node, (Collection<Object>) createdValue, collectionOwner);
        } else if (node instanceof MapNode) {
            final Map<Object, Object> mapInstance = (Map<Object, Object>) createdValue;
            final Object mapOwner = createItem.getOwner();
            populateMap((MapNode) node, mapInstance, mapOwner);
        } else if (node instanceof ArrayNode) {
            final Object arrayOwner = createItem.getOwner();
            populateArray((ArrayNode) node, createdValue, arrayOwner);
        }
    }

    private void populateArray(ArrayNode arrayNode, Object createdValue, Object arrayOwner) {
        final Node elementNode = arrayNode.getElementNode();

        // Field can be null when array is an element of a collection
        if (arrayNode.getField() != null) {
            ReflectionUtils.setField(arrayOwner, arrayNode.getField(), createdValue);
        }

        for (int i = 0; i < 2; i++) {
            final GeneratorResult<Object> generatorResult = generatorFacade.generateNodeValue(elementNode, createdValue);
            final Object elementValue = generatorResult.getValue();
            Array.set(createdValue, i, elementValue);

            enqueueChildrenOf(elementNode, generatorResult, queue);
        }
    }

    private void populateCollection(CollectionNode collectionNode, Collection<Object> collectionInstance, Object collectionOwner) {

        final Node elementNode = collectionNode.getElementNode();

        if (collectionNode.getField() != null)
            ReflectionUtils.setField(collectionOwner, collectionNode.getField(), collectionInstance);

        for (int i = 0; i < 2; i++) {
            final GeneratorResult<Object> generatorResult = generatorFacade.generateNodeValue(elementNode, collectionInstance);
            final Object elementValue = generatorResult.getValue();
            collectionInstance.add(elementValue);

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


        for (int i = 0; i < 2; i++) {
            final GeneratorResult<Object> generatorKeyResult = generatorFacade.generateNodeValue(keyNode, mapInstance);
            final GeneratorResult<Object> generatorValueResult = generatorFacade.generateNodeValue(valueNode, mapInstance);

            final Object mapKey = generatorKeyResult.getValue();
            final Object mapValue = generatorValueResult.getValue();
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

    private static void enqueueChildrenOf(Node node, GeneratorResult<?> result, Queue<CreateItem> queue) {
        if (!result.ignoreChildren()) {
            final Object owner = result.getValue();
            queue.addAll(node.getChildren().stream()
                    .map(it -> new CreateItem(it, owner))
                    .collect(toList()));
        }

    }

    <C> List<C> createList(Class<C> klass) {
        return null;
    }
}
