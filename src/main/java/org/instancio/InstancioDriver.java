package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.model.ArrayNode;
import org.instancio.model.ClassNode;
import org.instancio.model.CollectionNode;
import org.instancio.model.MapNode;
import org.instancio.model.Node;
import org.instancio.model.NodeContext;
import org.instancio.model.NodeFactory;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
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

    private final Hierarchy hierarchy = new Hierarchy();
    private final GeneratorMap generatorMap = new GeneratorMap();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();

    private final InstancioContext context;
    private final GeneratorFacade generatorFacade;

    public InstancioDriver(InstancioContext context) {
        this.context = context;
        this.generatorFacade = new GeneratorFacade(context);
    }

    <C> C createClassEntryPoint(Class<C> rootClass) {
        //final ClassNode rootClassNode = ClassNode.createRootNode(new NodeContext(context.getRootTypeMap()), rootClass);
        final NodeFactory nodeFactory = new NodeFactory();
        final Node rootNode = nodeFactory.createNode(new NodeContext(context.getRootTypeMap()), rootClass, null, null, null);

        final GeneratorResult<C> rootResult = generatorFacade.createInstanceOfClassNode(rootNode, null);
        final Object rootObject = rootResult.getValue();

        final Queue<CreateItem> queue = new ArrayDeque<>(rootResult.getFieldsToEnqueue());

        while (!queue.isEmpty()) {
            final CreateItem createItem = queue.poll();
            LOG.debug("-------------------\n\n-- {}\n", createItem);

            final Node node = createItem.getNode();

            if (node instanceof ClassNode) {
                final Field field = node.getField();

                if (field == null || context.isExcluded(field) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                final Object fieldOwner = createItem.getOwner();

                // TODO test.. what does it mean to return null GeneratorResult
                //
                final GeneratorResult<?> generatorResult = generatorFacade.create(createItem);
                if (generatorResult == null)
                    continue;

                final Object fieldValue = generatorResult.getValue();
                queue.addAll(generatorResult.getFieldsToEnqueue());
                ReflectionUtils.setField(fieldOwner, field, fieldValue);

            } else if (node instanceof CollectionNode) {
                final GeneratorResult<?> collectionResult = generatorFacade.create(createItem);
                if (collectionResult == null)
                    continue;

                final Collection<Object> collectionInstance = (Collection<Object>) collectionResult.getValue();
                queue.addAll(collectionResult.getFieldsToEnqueue());


                final CollectionNode collectionNode = (CollectionNode) node; // FIXME casting
                final Node elementNode = collectionNode.getElementNode();

                ReflectionUtils.setField(createItem.getOwner(), collectionNode.getField(), collectionInstance);

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> result = generatorFacade.createInstanceOfClassNode(elementNode, collectionInstance);

                    final Object createdValue = result.getValue();
                    collectionInstance.add(createdValue);

                    queue.addAll(elementNode.getChildren().stream()
                            .map(it -> new CreateItem(it, createdValue))
                            .collect(toList()));
                }

            } else if (node instanceof MapNode) {
                final GeneratorResult<?> mapResult = generatorFacade.create(createItem);
                if (mapResult == null)
                    continue;

                final Map<Object, Object> mapInstance = (Map<Object, Object>) mapResult.getValue();
                queue.addAll(mapResult.getFieldsToEnqueue());


                final MapNode mapNode = (MapNode) node; // FIXME casting
                final Node keyNode = mapNode.getKeyNode();
                final Node valueNode = mapNode.getValueNode();

                ReflectionUtils.setField(createItem.getOwner(), mapNode.getField(), mapInstance);

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> keyResult = generatorFacade.createInstanceOfClassNode(keyNode, mapInstance);
                    final Object mapEntryKey = keyResult.getValue();
                    queue.addAll(keyNode.getChildren().stream()
                            .map(it -> new CreateItem(it, mapEntryKey))
                            .collect(toList()));

                    final GeneratorResult<?> valueResult = generatorFacade.createInstanceOfClassNode(valueNode, mapInstance);
                    final Object mapEntryValue = valueResult.getValue();
                    queue.addAll(valueNode.getChildren().stream()
                            .map(it -> new CreateItem(it, mapEntryValue))
                            .collect(toList()));

                    mapInstance.put(mapEntryKey, mapEntryValue);
                }
            } else if (node instanceof ArrayNode) {
                final GeneratorResult<?> arrayResult = generatorFacade.create(createItem);
                if (arrayResult == null)
                    continue;

                final Object arrayInstance = arrayResult.getValue();
                queue.addAll(arrayResult.getFieldsToEnqueue());

                final ArrayNode arrayNode = (ArrayNode) node; // FIXME casting
                final Node elementNode = arrayNode.getElementNode();

                ReflectionUtils.setField(createItem.getOwner(), arrayNode.getField(), arrayInstance);

                for (int i = 0; i < 2; i++) {
                    final GeneratorResult<?> result = generatorFacade.createInstanceOfClassNode(elementNode, arrayInstance);

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
