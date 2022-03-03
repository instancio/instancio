package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

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
        final Map<String, Deque<Class<?>>> typeMap = new HashMap<>();

        if (context.getGenericTypes() != null) {
            final TypeVariable<Class<C>>[] typeParameters = klass.getTypeParameters();
            final Deque<Class<?>> genericTypes = context.getGenericTypes();

            for (TypeVariable<?> typeVariable : typeParameters) {
                Deque<Class<?>> d = new ArrayDeque<>();
                // XXX do we always map only the first element???
                d.add(genericTypes.pollFirst());
                typeMap.put(typeVariable.getName(), d);
            }
        }

        final GeneratorResult<C> rootResult = generatorFacade.createInstanceOfClass(klass, null, typeMap);
        final Object rootObject = rootResult.getValue();
        final Queue<CreateItem> queue = new ArrayDeque<>(rootResult.getFieldsToEnqueue());

        while (!queue.isEmpty()) {
            final CreateItem entry = queue.poll();
            LOG.debug("-------------------\n\n-- {}\n", entry);

            final Field field = entry.getField();

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
        final Field arrayField = createItem.getField();

        Class<?> componentType = arrayField.getType().getComponentType();

        if (arrayField.getGenericType() instanceof GenericArrayType) {
            String typeVariable = ((GenericArrayType) arrayField.getGenericType()).getGenericComponentType().getTypeName();
            Deque<Class<?>> types = createItem.getTypeMap().get(typeVariable);
            componentType = types.peekFirst(); // XXX
        }

        List<CreateItem> queueEntries = new ArrayList<>();
        for (int i = 0; i < Array.getLength(array); i++) {
            final GeneratorResult<?> generatorResult = generatorFacade.createInstanceOfClass(
                    componentType, owner, Collections.emptyMap()); // XXX typeMap?

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
        final Field collectionField = createItem.getField();

        LOG.debug("XXXX CreateItem: field: {}, typeMap: {}", createItem.getField().getName(), createItem.getTypeMap());
        LOG.debug("XXXX collectionToPopulate type vars: {} ", Arrays.toString(collectionToPopulate.getClass().getTypeParameters()));
        LOG.debug("XXXX owner type vars: {} ", Arrays.toString(owner.getClass().getTypeParameters()));

        // Lookup type variable by owner first... if not bound, try by collectionField
        // NOTE what if there are two?
        String typeVariable = "NONE";
        if (owner.getClass().getTypeParameters().length > 0) {
            typeVariable = owner.getClass().getTypeParameters()[0].getName();
            if (createItem.getTypeMap().get(typeVariable).isEmpty()) {
                typeVariable = collectionField.getType().getTypeParameters()[0].getName();
            }
        } else {
            typeVariable = collectionField.getType().getTypeParameters()[0].getName();
        }

        Deque<Class<?>> pTypes = createItem.getTypeMap().get(typeVariable);

        final List<CreateItem> queueEntries = new ArrayList<>();

        if (!pTypes.isEmpty()) {
            final Class<?> typeClass = pTypes.pollFirst();

            for (int i = 0; i < 2; i++) {
                final Map<String, Deque<Class<?>>> typeMap = createItem.getTypeMap();
                final Map<String, Deque<Class<?>>> typeMapCopy = new HashMap<>();

                typeMapCopy.put(typeVariable, new ArrayDeque<>(typeMap.get(typeVariable))); // XXX clone map

                final GeneratorResult<?> generatorResult = generatorFacade.createInstanceOfClass(typeClass, owner, typeMapCopy);
                collectionToPopulate.add(generatorResult.getValue());
                queueEntries.addAll(generatorResult.getFieldsToEnqueue());
            }
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
                // XXX typeMap?
                final GeneratorResult<?> keyResult = generatorFacade.createInstanceOfClass(keyClass, owner, Collections.emptyMap());
                final GeneratorResult<?> valueResult = generatorFacade.createInstanceOfClass(valueClass, owner, Collections.emptyMap());

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
