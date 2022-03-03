package org.instancio;

import org.instancio.generator.GeneratorMap;
import org.instancio.generator.ValueGenerator;
import org.instancio.reflection.ImplementationResolver;
import org.instancio.reflection.InterfaceImplementationResolver;
import org.instancio.util.Random;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        Optional<GeneratorResult<C>> optionalResult = attemptGenerateViaContent(field);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        }

        final Class<?> fieldType = field.getType();

        if (fieldType.isPrimitive()) {
            return new GeneratorResult<>((C) generatorMap.get(fieldType).generate(), Collections.emptyList());
        }

        final Object ancestor = hierarchy.getAncestorWithClass(owner, fieldType);

        if (ancestor != null) {
            LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                    owner.getClass().getSimpleName(), ancestor.getClass().getSimpleName());
            return null;
        }

        if (fieldType.isArray()) {
            Class<?> arrayType = field.getType().getComponentType();

            if (field.getGenericType() instanceof GenericArrayType) {
                String typeVariable = ((GenericArrayType) field.getGenericType()).getGenericComponentType().getTypeName();
                Deque<Class<?>> types = createItem.getTypeMap().get(typeVariable);
                arrayType = types.peekFirst(); // XXX
            }

            final ValueGenerator<?> generator = generatorMap.getArrayGenerator(arrayType);
            final Object arrayObject = generator.generate();

            return new GeneratorResult<>((C) arrayObject, Collections.emptyList()); // XXX pass in type map
        }

        final ValueGenerator<?> generator = generatorMap.get(fieldType);

        final C value;
        List<CreateItem> fields = Collections.emptyList();

        if (generator == null) {

            if (fieldType.isInterface()) {
                return resolveImplementationAndGenerate(owner, fieldType);
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
                LOG.debug("Generator not found for '{}': instantiating via constructor", fieldType.getName());
                value = (C) ReflectionUtils.instantiate(fieldType);

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
            }

            // Do not collect fields for JDK classes
            if (!fieldType.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {

                fields = Arrays.stream(fieldType.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .map(f -> {
                            Map<String, Deque<Class<?>>> map;
                            if (typeVariableMap.isEmpty()) {
                                map = new HashMap<>();
                                final Deque<Class<?>> parameterizedTypes = ReflectionUtils.getParameterizedTypes(f);
                                if (!parameterizedTypes.isEmpty()) {
                                    String typeVar = f.getType().getTypeParameters()[0].getName();
                                    map.put(typeVar, parameterizedTypes);
                                }
                            } else {
                                map = typeVariableMap;
                            }
                            return new CreateItem(f, value, map);
                        }) //TODO check pTypes
                        .collect(toList());
            }
        } else {
            LOG.debug("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), fieldType.getName());

            // If we already know how to generate this object, we don't need to collect its fields
            value = (C) generator.generate();
            LOG.trace("Generated {} using '{}' generator ", value, generator.getClass().getSimpleName());
        }

        hierarchy.setAncestorOf(value, owner);
        return new GeneratorResult<>(value, fields);

    }

    <C> GeneratorResult<C> createInstanceOfClass(Class<C> klass, Object owner, Map<String, Deque<Class<?>>> typeMap) {

        if (klass.isPrimitive()) {
            return new GeneratorResult<>((C) generatorMap.get(klass).generate(), Collections.emptyList());
        }

        final Object ancestor = hierarchy.getAncestorWithClass(owner, klass);

        if (ancestor != null) {
            LOG.debug("{} has a circular dependency to {}. Not setting field value.",
                    owner.getClass().getSimpleName(), ancestor.getClass().getSimpleName());
            return null;
        }


        final ValueGenerator<?> generator = generatorMap.get(klass);

        final C value;
        List<CreateItem> fields = Collections.emptyList();

        if (generator == null) {

            if (klass.isInterface()) {
                return resolveImplementationAndGenerate(owner, klass);
            }

            LOG.debug("Generator not found for '{}': will attempt to instantiate via constructor", klass.getName());
            value = ReflectionUtils.instantiate(klass);

            // Do not collect fields for JDK classes
            if (!klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
                // XXX temporarily disable inheritance
                //fields = ReflectionUtils.getDeclaredAndSuperclassFields(klass).stream()
                fields = Arrays.stream(klass.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .map(f -> {
                            Map<String, Deque<Class<?>>> fieldTypeMap = new HashMap<>();
                            if (typeMap.isEmpty()) {
                                Deque<Class<?>> typeClasses = ReflectionUtils.getParameterizedTypes(f);
                                if (!typeClasses.isEmpty()) {
                                    final String typeVariable = f.getType().getTypeParameters()[0].getName();
                                    fieldTypeMap.put(typeVariable, typeClasses);
                                }
                            } else {
                                fieldTypeMap = typeMap;
                            }
                            return new CreateItem(f, value, fieldTypeMap);
                        })
                        .collect(toList());
            }
        } else {
            LOG.debug("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), klass.getName());

            // If we already know how to generate this object, we don't need to collect its fields
            value = (C) generator.generate();
            LOG.trace("Generated {} using '{}' generator ", value, generator.getClass().getSimpleName());
        }

        hierarchy.setAncestorOf(value, owner);
        return new GeneratorResult<>(value, fields);
    }

    /**
     * Resolve an implementation class for the given interface and attempt to generate it.
     * This method should not be called for JDK classes, such as Collection interfaces.
     */
    private <C> GeneratorResult<C> resolveImplementationAndGenerate(Object owner, Class<?> interfaceClass) {
        Verify.isTrue(!Collection.class.isAssignableFrom(interfaceClass)
                && !Map.class.isAssignableFrom(interfaceClass), "Must not be a collection interface!");

        LOG.debug("No generator for interface '{}'", interfaceClass.getName());

        Class<?> implementor = implementationResolver.resolve(interfaceClass).orElse(null);
        if (implementor == null) {
            LOG.debug("Interface '{}' has no implementation", interfaceClass.getName());
            return null;
        }

        return (GeneratorResult<C>) createInstanceOfClass(implementor, owner, Collections.emptyMap()); // XXX typeMap?
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     * <p>
     * TODO: hierarchy.setAncestorOf(value, owner) must be done for all generated objects
     *  unless they are from JDK classes
     */
    private <C> Optional<GeneratorResult<C>> attemptGenerateViaContent(Field field) {
        GeneratorResult<C> result = null;
        if (context.isNullable(field) && Random.trueOrFalse()) {
            // We can return a null 'GeneratorResult' or a null 'GeneratorResult.value'
            // Returning a null 'GeneratorResult.value' will ensure that a field value
            // will be overwritten with null. Otherwise, field value would retain its
            // old value (if one was assigned).
            result = new GeneratorResult<>(null, Collections.emptyList());
        }
        if (context.getUserSuppliedFieldValueGenerators().containsKey(field)) {
            ValueGenerator<?> generator = context.getUserSuppliedFieldValueGenerators().get(field);
            result = new GeneratorResult<>((C) generator.generate(), Collections.emptyList());
        }
        if (context.getUserSuppliedClassValueGenerators().containsKey(field.getType())) {
            ValueGenerator<?> generator = context.getUserSuppliedClassValueGenerators().get(field.getType());
            // TODO need fields to enqueue instead of empty list??
            result = new GeneratorResult<>((C) generator.generate(), Collections.emptyList());
        }
        return Optional.ofNullable(result);
    }

}
