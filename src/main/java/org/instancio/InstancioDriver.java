package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.model.ClassNode;
import org.instancio.model.FieldNode;
import org.instancio.model.Node;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

        final GeneratorResult<C> rootResult = generatorFacade.createInstanceOfClass(rootClass, null);
        final Object rootObject = rootResult.getValue();

        final Queue<CreateItem> queue = new ArrayDeque<>(rootResult.getFieldsToEnqueue());

        while (!queue.isEmpty()) {
            final CreateItem createItem = queue.poll();
            LOG.debug("-------------------\n\n-- {}\n", createItem);

            final Node node = createItem.getNode();

            if (node instanceof FieldNode) {
                final FieldNode fieldNode = (FieldNode) node; // FIXME
                final Field field = fieldNode.getField();

                if (context.isExcluded(field) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                final Object fieldOwner = createItem.getOwner();
                final Class<?> fieldType = field.getType();

                // TODO test.. what does it mean to return null GeneratorResult
                //
                final GeneratorResult<?> generatorResult = generatorFacade.create(createItem);
                if (generatorResult == null)
                    continue;

                final Object fieldValue = generatorResult.getValue();
                queue.addAll(generatorResult.getFieldsToEnqueue());
                ReflectionUtils.setField(fieldOwner, field, fieldValue);

                if (fieldType.isArray()) {
                    queue.addAll(populateArray(createItem, fieldValue));
                }
//                else if (Collection.class.isAssignableFrom(fieldType)) {
//                    queue.addAll(populateCollection(createItem, (Collection<Object>) fieldValue));
//                } else if (Map.class.isAssignableFrom(fieldType)) {
//                    queue.addAll(populateMap(fieldOwner, field, (Map<Object, Object>) fieldValue));
//                }
            } else {
                final ClassNode classNode = (ClassNode) node; // FIXME
                final FieldNode parent = (FieldNode) classNode.getParent();

                if (Collection.class.isAssignableFrom(parent.getActualFieldType())) {
                    final Collection<Object> owner = (Collection<Object>) createItem.getOwner();

                    for (int i = 0; i < 2; i++) {
                        final GeneratorResult<?> result = generatorFacade.createInstanceOfClass(classNode.getKlass(), owner);

                        final Object createdValue = result.getValue();
                        owner.add(createdValue);
                        //queue.addAll(result.getFieldsToEnqueue()); // XXX this child list is not complete!

                        queue.addAll(classNode.getChildren().stream()
                                .map(it -> new CreateItem(it, createdValue))
                                .collect(toList()));
                    }
                } else if (Map.class.isAssignableFrom(parent.getActualFieldType())) {
                    final Map<Object, Object> owner = (Map<Object, Object>) createItem.getOwner();

                    final Map<Type, TypeVariable<?>> reverseTypeMap = parent.getTypeMap()
                            .entrySet().stream()
                            .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));

                    final TypeVariable<?> typeVariable = reverseTypeMap.get(classNode.getKlass());

                    for (int i = 0; i < 2; i++) {
                        final GeneratorResult<?> result = generatorFacade.createInstanceOfClass(classNode.getKlass(), owner);

                        final Object createdValue = result.getValue();
                        //owner.add(createdValue);
                        //queue.addAll(result.getFieldsToEnqueue()); // XXX this child list is not complete!

                        queue.addAll(classNode.getChildren().stream()
                                .map(it -> new CreateItem(it, createdValue))
                                .collect(toList()));
                    }

                }
            }
        }

        return (C) rootObject;
    }

    private List<CreateItem> populateArray(CreateItem createItem, Object array) {
        if (array == null || Array.getLength(array) == 0) {
            return Collections.emptyList();
        }
        final Object owner = createItem.getOwner();
        final FieldNode node = (FieldNode) createItem.getNode();
        final Class<?> componentType = node.getArrayType();

        List<CreateItem> queueEntries = new ArrayList<>();
        for (int i = 0; i < Array.getLength(array); i++) {
            GeneratorResult<?> generatorResult = generatorFacade.createInstanceOfClass(componentType, owner);
            Array.set(array, i, generatorResult.getValue());
            queueEntries.addAll(generatorResult.getFieldsToEnqueue());
        }
        return queueEntries;
    }

    private List<CreateItem> populateCollection(CreateItem createItem, Collection<Object> collectionToPopulate) {
        if (collectionToPopulate == null) {
            return Collections.emptyList();
        }

        final Object owner = createItem.getOwner();
        final FieldNode node = (FieldNode) createItem.getNode(); // FIXME

        final List<CreateItem> nextNodes = node.getChildren()
                .stream()
                .map(n -> new CreateItem(n, collectionToPopulate))
                .collect(toList());


        final List<CreateItem> queueEntries = new ArrayList<>();

        final Class<?> collectionEntryClass = node.getCollectionType();

        //final ClassNode collectionChild = (ClassNode) createItem.getNode().getChildren().get(0);


        for (int i = 0; i < 2; i++) {

            GeneratorResult<?> result = generatorFacade.createInstanceOfClass(collectionEntryClass, owner);
            //GeneratorResult<?> result = generatorFacade.createInstanceOfClass(collectionChild.getKlass(), owner);

            collectionToPopulate.add(result.getValue());
            queueEntries.addAll(result.getFieldsToEnqueue());
            //queueEntries.addAll(nextNodes);
        }

        return queueEntries;
    }

    private List<CreateItem> populateMap(Object owner, Field mapField, Map<Object, Object> map) {
        if (map == null) {
            return Collections.emptyList();
        }

        final Optional<Pair<Class<?>, Class<?>>> typeClassOpt = ReflectionUtils.getMapType(mapField);
        final List<CreateItem> queueEntries = new ArrayList<>();

        if (typeClassOpt.isPresent()) {
            final Class<?> keyClass = typeClassOpt.get().getLeft();
            final Class<?> valueClass = typeClassOpt.get().getRight();

            for (int i = 0; i < 2; i++) {
                final GeneratorResult<?> keyResult = generatorFacade.createInstanceOfClass(keyClass, owner);
                final GeneratorResult<?> valueResult = generatorFacade.createInstanceOfClass(valueClass, owner);

                map.put(keyResult.getValue(), valueResult.getValue());
                queueEntries.addAll(keyResult.getFieldsToEnqueue());
                queueEntries.addAll(valueResult.getFieldsToEnqueue());
            }
        }

        return queueEntries;
    }

    <C> List<C> createList(Class<C> klass) {
        return null;
    }
}
