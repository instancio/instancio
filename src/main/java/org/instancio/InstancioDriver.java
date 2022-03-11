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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

class InstancioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioDriver.class);

    private final InstancioContext context;
    private final GeneratorFacade generatorFacade;

    public InstancioDriver(InstancioContext context) {
        this.context = context;
        this.generatorFacade = new GeneratorFacade(context);
    }

    <C> C createClassEntryPoint(Class<C> rootClass) {
        final NodeFactory nodeFactory = new NodeFactory();
        final Node rootNode = nodeFactory.createNode(new NodeContext(context.getRootTypeMap()), rootClass, null, null, null);

        final GeneratorResult<C> rootResult = generatorFacade.generateNodeValue(rootNode, null);
        final Object rootObject = rootResult.getValue();

        final Queue<CreateItem> queue = new ArrayDeque<>(rootResult.getFieldsToEnqueue());

        while (!queue.isEmpty()) {
            final CreateItem createItem = queue.poll();
            LOG.debug("-------------------\n\n-- {}\n", createItem);

            final Node node = createItem.getNode();
            final Field field = node.getField();

            if (field == null || context.isExcluded(field) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            final GeneratorResult<?> createdItem = generatorFacade.generateNodeValue(createItem.getNode(), createItem.getOwner());

            if (createdItem == null) {
                continue;
            }

            queue.addAll(createdItem.getFieldsToEnqueue());

            if (node instanceof ClassNode) {
                final Object fieldValue = createdItem.getValue();

                ReflectionUtils.setField(createItem.getOwner(), field, fieldValue);

            } else if (node instanceof CollectionNode) {
                final Collection<Object> collectionInstance = (Collection<Object>) createdItem.getValue();
                final CollectionNode collectionNode = (CollectionNode) node;
                final Node elementNode = collectionNode.getElementNode();

                ReflectionUtils.setField(createItem.getOwner(), collectionNode.getField(), collectionInstance);

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> result = generatorFacade.generateNodeValue(elementNode, collectionInstance);
                    final Object createdValue = result.getValue();
                    collectionInstance.add(createdValue);

                    queue.addAll(elementNode.getChildren().stream()
                            .map(it -> new CreateItem(it, createdValue))
                            .collect(toList()));
                }

            } else if (node instanceof MapNode) {
                final Map<Object, Object> mapInstance = (Map<Object, Object>) createdItem.getValue();
                final MapNode mapNode = (MapNode) node;
                final Node keyNode = mapNode.getKeyNode();
                final Node valueNode = mapNode.getValueNode();

                // How to collect children when map's 'value' is another map?
                // (NOTE this probably applies to nested Collection and Array nodes too)
                //
                // 'value' is a MapNode, therefore its 'getChildren()' would return an empty list
                // option1: have MapNode.children return key/value nodes' children. don't add children of key/value node directly
                //          Risk: processing duplicate children if they all end up the queue
                // option2: check if value node is a Map/Collection/Array node... then get element/key/value nodes children (UGLY)

                ReflectionUtils.setField(createItem.getOwner(), mapNode.getField(), mapInstance);

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> keyResult = generatorFacade.generateNodeValue(keyNode, mapInstance);
                    final Object mapEntryKey = keyResult.getValue();
                    queue.addAll(keyNode.getChildren().stream()
                            .map(it -> new CreateItem(it, mapEntryKey))
                            .collect(toList()));

                    final GeneratorResult<?> valueResult = generatorFacade.generateNodeValue(valueNode, mapInstance);
                    final Object mapEntryValue = valueResult.getValue();

                    // ======================
                    // XXX ugly hack.. if value node is also a MapNode, collect children from its valueNode
                    //  (this needs to be done for nested Lists and Arrays too
//
//                    List<CreateItem> nextItems = new ArrayList<>();
//
//                    if (valueNode instanceof MapNode) {
//                        Node nestedMapValueNode = ((MapNode) valueNode).getValueNode();
//                        nextItems.addAll(nestedMapValueNode.getChildren().stream()
//                                .map(it -> new CreateItem(it, mapEntryValue))
//                                .collect(toList()));
//
//                        valueNode.getChildren();
//                    }
//
//                    queue.addAll(nextItems);

                    // =======================

                    queue.addAll(valueNode.getChildren().stream()
                            .map(it -> new CreateItem(it, mapEntryValue))
                            .collect(toList()));


                    mapInstance.put(mapEntryKey, mapEntryValue);
                }
            } else if (node instanceof ArrayNode) {
                final Object arrayInstance = createdItem.getValue();
                final ArrayNode arrayNode = (ArrayNode) node;
                final Node elementNode = arrayNode.getElementNode();

                ReflectionUtils.setField(createItem.getOwner(), arrayNode.getField(), arrayInstance);

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> result = generatorFacade.generateNodeValue(elementNode, arrayInstance);

                    final Object createdValue = result.getValue();
                    Array.set(arrayInstance, i, createdValue);

                    queue.addAll(elementNode.getChildren().stream()
                            .map(it -> new CreateItem(it, createdValue))
                            .collect(toList()));
                }

            }

        }
        return (C) rootObject;
    }

    <C> List<C> createList(Class<C> klass) {
        return null;
    }
}
