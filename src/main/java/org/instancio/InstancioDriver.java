package org.instancio;

import experimental.reflection.nodes.ClassNode;
import experimental.reflection.nodes.FieldNode;
import org.instancio.generator.GeneratorMap;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static java.util.stream.Collectors.toCollection;
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

    <C> C create(Class<C> klass) {

        final GeneratorResult<C> rootResult = generatorFacade.createInstanceOfClass(klass, null);
        final Object rootObject = rootResult.getValue();

        final Queue<CreateItem> queue = new ArrayDeque<>(rootResult.getFieldsToEnqueue());

        while (!queue.isEmpty()) {
            final CreateItem entry = queue.poll();
            LOG.debug("-------------------\n\n-- {}\n", entry);

            final FieldNode node = entry.getFieldNode();
            final Field field = node.getField();

            if (context.isExcluded(field) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            final Object fieldOwner = entry.getOwner();
            final Class<?> fieldType = field.getType();

            // TODO test.. what does it mean to return null GeneratorResult
            //
            final GeneratorResult<?> generatorResult = generatorFacade.create(entry);
            if (generatorResult == null)
                continue;

            final Object fieldValue = generatorResult.getValue();
            queue.addAll(generatorResult.getFieldsToEnqueue());
            ReflectionUtils.setField(fieldOwner, field, fieldValue);

            if (fieldType.isArray()) {
                queue.addAll(populateArray(entry, fieldValue));
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                queue.addAll(populateCollection(entry, (Collection<Object>) fieldValue));
            } else if (Map.class.isAssignableFrom(fieldType)) {
                queue.addAll(populateMap(fieldOwner, field, (Map<Object, Object>) fieldValue));
            }
        }

        return (C) rootObject;
    }

    private List<CreateItem> populateArray(CreateItem createItem, Object array) {
        if (array == null || Array.getLength(array) == 0) {
            return Collections.emptyList();
        }
        final Object owner = createItem.getOwner();
        final Field arrayField = createItem.getFieldNode().getField();
        final Class<?> componentType = arrayField.getType().getComponentType();

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
        final List<CreateItem> nextNodes = createItem.getFieldNode().getChildren()
                .stream()
                .map(n -> new CreateItem(n, collectionToPopulate))
                .collect(toList());


        final List<CreateItem> queueEntries = new ArrayList<>();

        final Class<?> collectionEntryClass = createItem.getFieldNode().getCollectionType();

        for (int i = 0; i < 2; i++) {

            GeneratorResult<?> result = generatorFacade.createInstanceOfClass(collectionEntryClass, owner);

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
