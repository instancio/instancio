package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.generator.ValueGenerator;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.Random;
import org.instancio.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

class GeneratorFacade {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorFacade.class);

    private static final String JAVA_PKG_PREFIX = "java";

    private final Hierarchy hierarchy = new Hierarchy();
    private final GeneratorMap generatorMap = new GeneratorMap();
    private final ImplementationResolver implementationResolver = new InterfaceImplementationResolver();
    private final InstancioContext context;

    public GeneratorFacade(InstancioContext context) {
        this.context = context;
    }

    <C> GeneratorResult<C> create(CreateItem createItem) {
        final Field field = createItem.getField();
        final Object owner = createItem.getOwner();

        LOG.debug("Creating value for field '{}', genericType: {}, typeMap: {}",
                field.getName(), field.getGenericType(), createItem.getTypeMap());

        if (context.isNullable(field) && Random.trueOrFalse()) {
            return null; // TODO test.. what does it mean to return null GeneratorResult
        }

        if (context.getUserSuppliedFieldValueGenerators().containsKey(field)) {
            final ValueGenerator<?> generator = context.getUserSuppliedFieldValueGenerators().get(field);

            return new GeneratorResult<>((C) generator.generate(), Collections.emptyList());
        }

        if (context.getUserSuppliedClassValueGenerators().containsKey(field.getType())) {
            final ValueGenerator<?> generator = context.getUserSuppliedClassValueGenerators().get(field.getType());

            // TODO need fields to enqueue instead of empty list??
            return new GeneratorResult<>((C) generator.generate(), Collections.emptyList());
        }

        //XXX return (GeneratorResult<C>) createInstanceOfClass(field.getType(), owner, new ArrayDeque<>());
        Class<?> klass = field.getType();
        final Object ancestor = hierarchy.getAncestorWithClass(owner, klass);

        if (ancestor != null) {
            LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                    owner.getClass().getSimpleName(), ancestor.getClass().getSimpleName());
            return null;
        }

        if (klass.isPrimitive()) {
            return new GeneratorResult<>((C) generatorMap.get(klass).generate(), Collections.emptyList());
        }
        if (klass.isArray()) {
            Class<?> arrayType = field.getType().getComponentType();

            if (field.getGenericType() instanceof GenericArrayType) {
                String typeVariable = ((GenericArrayType) field.getGenericType()).getGenericComponentType().getTypeName();
                Deque<Class<?>> types = createItem.getTypeMap().get(typeVariable);
                arrayType = types.peekFirst(); // XXX
            }

            final ValueGenerator<?> generator = generatorMap.getArrayGenerator(arrayType);
            final Object arrayObject = generator.generate();

            return new GeneratorResult<>((C)arrayObject, Collections.emptyList()); // XXX pass in type map
        }

        final ValueGenerator<?> generator = generatorMap.get(klass);

        final C value;
        List<CreateItem> fields = Collections.emptyList();

        if (generator == null) {

            if (klass.isInterface()) {
                LOG.debug("No generator for interface '{}'", klass.getName());
                final Class<?> implementor = getConcreteClass(klass);
                if (implementor == null) {
                    LOG.debug("Interface '{}' has no implementation", klass.getName());
                    return null;
                }

                return (GeneratorResult<C>) createInstanceOfClass(implementor, owner);
            }

            LOG.debug("XXX field {}, typeName: {}, typeMap: {}",
                    field.getName(), field.getGenericType().getTypeName(), createItem.getTypeMap());

            final Deque<Class<?>> genericTypes = createItem.getTypeMap().get(field.getGenericType().getTypeName());


            if (genericTypes != null) {

                // XXX toggle pollFirst and peekFirst to break either one or other half of the tests
//                final Class<?> typeToCreate = genericTypes.pollFirst();
                final Class<?> typeToCreate = genericTypes.peekFirst();
                return (GeneratorResult<C>) createInstanceOfClass(typeToCreate, owner, createItem.getTypeMap());
            } else {
                LOG.debug("Generator not found for '{}': instantiating via constructor", klass.getName());
                value = (C) ReflectionUtils.instantiate(klass);

            }

            // NOTE if it's generic, then create a Map of TypeVariable to Class, e.g. {"T" -> String.class}
            final Map<String, Deque<Class<?>>> typeVariableMap = new HashMap<>();
            //typeVariableMap.putAll(createItem.getTypeMap()); // NOTE need this?

            if (field.getGenericType() instanceof ParameterizedType) {

                final Deque<Class<?>> parameterizedTypes = ReflectionUtils.getParameterizedTypes(field);

                final Deque<Class<?>> pTypes = genericTypes != null && !genericTypes.isEmpty()
                        ? genericTypes
                        : parameterizedTypes;

                typeVariableMap.put(
                        field.getType().getTypeParameters()[0].getName(),
                        pTypes); // XXX picking only last is not correct.. does not work for Foo<Bar<Baz<String>>>

                //LOG.debug("ZZZZZ pTypes {}", pTypes);
            }

            // Do not collect fields for JDK classes
            if (!klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
                fields = Arrays.stream(klass.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .map(f -> {
                            Map<String, Deque<Class<?>>> map;
                            if (typeVariableMap.isEmpty()) {
                                map = new HashMap<>();
                                map.put("[T]", ReflectionUtils.getParameterizedTypes(f));
                            } else {
                                map = typeVariableMap;
                            }
                            return new CreateItem(f, value, map);
                        }) //TODO check pTypes
                        .collect(toList());
            }
        } else {
            LOG.debug("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), klass.getName());

            // If we already know how to generate this object,
            // we don't need to collect its fields
            value = (C) generator.generate();
            LOG.trace("Generated {} using '{}' generator ", value, generator.getClass().getSimpleName());
        }

        hierarchy.setAncestorOf(value, owner);
        return new GeneratorResult<>(value, fields);

    }

    <C> GeneratorResult<C> createInstanceOfClass(Class<C> klass, Object owner, Map<String, Deque<Class<?>>> typeMap) {
        final Object ancestor = hierarchy.getAncestorWithClass(owner, klass);

        if (ancestor != null) {
            LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                    owner.getClass().getSimpleName(), ancestor.getClass().getSimpleName());
            return null;
        }

        if (klass.isPrimitive()) {
            return new GeneratorResult<>((C) generatorMap.get(klass).generate(), Collections.emptyList());
        }

        final ValueGenerator<?> generator = generatorMap.get(klass);

        final C value;
        List<CreateItem> fields = Collections.emptyList();

        if (generator == null) {

            if (klass.isInterface()) {
                LOG.debug("No generator for interface '{}'", klass.getName());
                final Class<?> implementor = getConcreteClass(klass);
                if (implementor == null) {
                    LOG.debug("Interface '{}' has no implementation", klass.getName());
                    return null;
                }

                return (GeneratorResult<C>) createInstanceOfClass(implementor, owner);
            }

            LOG.debug("Generator not found for '{}': will attempt to instantiate via constructor", klass.getName());
            value = ReflectionUtils.instantiate(klass);

            // Do not collect fields for JDK classes
            if (!klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
                // XXX temporarily disable inheritance
                //fields = ReflectionUtils.getDeclaredAndSuperclassFields(klass).stream()
                fields = Arrays.stream(klass.getDeclaredFields())
                        //.map(field -> new CreateItem(field, value, typeMap))
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .map(field -> {
                            Map<String, Deque<Class<?>>> fieldTypeMap = new HashMap<>();
                            if (typeMap.isEmpty()) {
                                Deque<Class<?>> typeClasses = ReflectionUtils.getParameterizedTypes(field);
                                if (!typeClasses.isEmpty()) {
                                    final String typeVariable = field.getType().getTypeParameters()[0].getName();
                                    fieldTypeMap.put(typeVariable, typeClasses);
                                    //fieldTypeMap.put("[T]", typeClasses);// XXX hardcoded for now
                                }
                            } else {
                                fieldTypeMap = typeMap;
                            }
                            return new CreateItem(field, value, fieldTypeMap);
                        })
                        .collect(toList());
            }
        } else {
            LOG.debug("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), klass.getName());

            // If we already know how to generate this object,
            // we don't need to collect its fields
            value = (C) generator.generate();
            LOG.trace("Generated {} using '{}' generator ", value, generator.getClass().getSimpleName());
        }

        hierarchy.setAncestorOf(value, owner);
        return new GeneratorResult<>(value, fields);
    }


    <C> GeneratorResult<C> createInstanceOfClass(Class<C> klass, Object owner) {
        return createInstanceOfClass(klass, owner, new HashMap<>()); // empty typeMap
    }

    private <C> Class<?> getConcreteClass(Class<C> klass) {
        final Set<Class<?>> implementors = implementationResolver.resolve(klass);
        if (implementors.size() != 1) {
            LOG.debug("Found {} implementors for class {}: {}. Will not instantiate.",
                    implementors.size(), klass.getName(), implementors);

            return null;
        }

        return implementors.iterator().next();
    }
}
