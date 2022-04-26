/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal;

import org.instancio.Generator;
import org.instancio.Generators;
import org.instancio.OnCompleteCallback;
import org.instancio.Random;
import org.instancio.Selector;
import org.instancio.Selector.SelectorType;
import org.instancio.SelectorGroup;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.array.ArrayGenerator;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.PropertiesLoader;
import org.instancio.settings.Settings;
import org.instancio.util.ObjectUtils;
import org.instancio.util.SeedUtil;
import org.instancio.util.TypeUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.instancio.util.ObjectUtils.defaultIfNull;
import static org.instancio.util.ReflectionUtils.getField;

public class ModelContext<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ModelContext.class);

    private final Type rootType;
    private final Class<T> rootClass;
    private final List<Class<?>> rootTypeParameters;
    private final Set<Field> ignoredFields;
    private final Set<Field> nullableFields;
    private final Set<Class<?>> ignoredClasses;
    private final Set<Class<?>> nullableClasses;
    private final Map<Field, Generator<?>> userSuppliedFieldGenerators;
    private final Map<Class<?>, Generator<?>> userSuppliedClassGenerators;
    private final Map<Field, OnCompleteCallback<?>> userSuppliedFieldCallbacks;
    private final Map<Class<?>, OnCompleteCallback<?>> userSuppliedClassCallbacks;
    private final Map<SelectorGroup, OnCompleteCallback<?>> onCompleteCallbacks;
    private final Map<Class<?>, Class<?>> classSubtypeMap;
    private final Map<Field, Class<?>> fieldSubtypeMap;
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Settings settings;
    private final Integer seed;
    private final Random random;
    private final Set<SelectorGroup> ignoredSelectorGroups;
    private final Set<SelectorGroup> nullableSelectorGroups;
    private final Map<SelectorGroup, Class<?>> subtypeSelectors;
    private final Map<SelectorGroup, Generator<?>> generatorSelectors;
    private final Map<SelectorGroup, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecSelectors;
    private final Generators generators;

    private ModelContext(final Builder<T> builder) {
        this.rootType = builder.rootType;
        this.rootClass = builder.rootClass;
        this.rootTypeParameters = Collections.unmodifiableList(builder.rootTypeParameters);
        this.ignoredFields = new HashSet<>();
        this.nullableFields = new HashSet<>();
        this.ignoredClasses = new HashSet<>();
        this.nullableClasses = new HashSet<>();
        this.userSuppliedFieldGenerators = new HashMap<>();
        this.userSuppliedClassGenerators = new HashMap<>();
        this.userSuppliedFieldCallbacks = new HashMap<>();
        this.userSuppliedClassCallbacks = new HashMap<>();
        this.fieldSubtypeMap = new HashMap<>();
        this.classSubtypeMap = new HashMap<>();
        this.ignoredSelectorGroups = Collections.unmodifiableSet(builder.ignoredSelectorGroups);
        this.nullableSelectorGroups = Collections.unmodifiableSet(builder.nullableSelectorGroups);
        this.subtypeSelectors = Collections.unmodifiableMap(builder.subtypeSelectors);
        this.generatorSelectors = Collections.unmodifiableMap(builder.generatorSelectors);
        this.generatorSpecSelectors = Collections.unmodifiableMap(builder.generatorSpecSelectors);
        this.onCompleteCallbacks = Collections.unmodifiableMap(builder.onCompleteCallbacks);

        this.rootTypeMap = rootType instanceof ParameterizedType || rootType instanceof GenericArrayType
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(buildRootTypeMap(rootClass, builder.rootTypeParameters));

        this.settings = Settings.defaults()
                .merge(Settings.from(new PropertiesLoader().load("instancio.properties")))
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings)
                .lock();

        this.seed = builder.seed;
        this.random = resolveRandom(seed);
        this.generators = new Generators(new GeneratorContext(settings, random));

        builder.onCompleteCallbacks.forEach(this::putCallbackSelectors);
        builder.generatorSpecSelectors.forEach(this::putGeneratorSelectors);
        putAllSubtypeSelectors(builder.subtypeSelectors);
        putAllUserSuppliedGenerators(builder.generatorSelectors);
        putIgnored(builder.ignoredSelectorGroups);
        putNullable(builder.nullableSelectorGroups);
    }

    private void putAllSubtypeSelectors(final Map<SelectorGroup, Class<?>> groups) {
        groups.forEach((selectorGroup, subtype) -> {
            for (Selector selector : selectorGroup.getSelectors()) {
                if (selector.selectorType() == SelectorType.FIELD) {
                    final Class<?> targetType = defaultIfNull(selector.getTargetClass(), this.rootClass);
                    final Field field = getField(targetType, selector.getFieldName());
                    // TODO validate
                    fieldSubtypeMap.put(field, subtype);
                } else {
                    ApiValidator.validateSubtypeMapping(selector.getTargetClass(), subtype);
                    classSubtypeMap.put(selector.getTargetClass(), subtype);
                }
            }
        });
    }

    private static Random resolveRandom(@Nullable final Integer userSuppliedSeed) {
        if (userSuppliedSeed != null) {
            return new DefaultRandom(userSuppliedSeed);
        }
        // If running under JUnit extension, use the provider supplied by the extension
        return ObjectUtils.defaultIfNull(
                ThreadLocalRandom.getInstance().get(),
                () -> new DefaultRandom(SeedUtil.randomSeed()));
    }

    private void putAllUserSuppliedGenerators(final Map<SelectorGroup, Generator<?>> generatorSelectors) {
        generatorSelectors.forEach((selectorGroup, generator) -> {
            for (Selector selector : selectorGroup.getSelectors()) {
                mapSelectedToGenerator(selector, generator);
            }
        });
    }

    private void putCallbackSelectors(final SelectorGroup selectorGroup, final OnCompleteCallback<?> callback) {
        for (Selector selector : selectorGroup.getSelectors()) {
            if (selector.selectorType() == SelectorType.FIELD) {
                final Class<?> targetType = defaultIfNull(selector.getTargetClass(), this.rootClass);
                final Field field = getField(targetType, selector.getFieldName());
                this.userSuppliedFieldCallbacks.put(field, callback);
            } else {
                this.userSuppliedClassCallbacks.put(selector.getTargetClass(), callback);
            }
        }
    }

    private void putGeneratorSelectors(final SelectorGroup selectorGroup, final Function<Generators, ? extends GeneratorSpec<?>> genFn) {
        for (Selector selector : selectorGroup.getSelectors()) {
            // Do not share generator instances among selectors of a selector group.
            // For example, array generators are created for each component type.
            // Therefore, using 'gen.array().length(10)' would fail when selectors are different array types.
            final Generator<?> generator = (Generator<?>) genFn.apply(generators);
            mapSelectedToGenerator(selector, generator);
        }
    }

    private void mapSelectedToGenerator(final Selector selector, Generator<?> generator) {
        if (selector.selectorType() == SelectorType.FIELD) {
            final Class<?> targetType = defaultIfNull(selector.getTargetClass(), this.rootClass);
            final Field field = getField(targetType, selector.getFieldName());

            // TODO refactor to remove the isArray conditional
            if (field.getType().isArray() && generator instanceof ArrayGenerator) {
                ((ArrayGenerator<?>) generator).type(field.getType());
            }
            this.userSuppliedFieldGenerators.put(field, generator);
        } else {
            final Class<?> userSpecifiedClass = generator.targetClass().orElse(selector.getTargetClass());
            if (selector.getTargetClass().isArray() && generator instanceof ArrayGenerator) {
                ((ArrayGenerator<?>) generator).type(userSpecifiedClass);
            }
            if (userSpecifiedClass != selector.getTargetClass()) {
                this.classSubtypeMap.put(selector.getTargetClass(), userSpecifiedClass);
            }
            this.userSuppliedClassGenerators.put(selector.getTargetClass(), generator);
        }
    }

    private void putIgnored(Set<SelectorGroup> ignoredSelectorGroups) {
        for (SelectorGroup selectorGroup : ignoredSelectorGroups) {
            for (Selector target : selectorGroup.getSelectors()) {
                if (target.selectorType() == SelectorType.FIELD) {
                    final Class<?> targetType = ObjectUtils.defaultIfNull(target.getTargetClass(), this.rootClass);
                    this.ignoredFields.add(getField(targetType, target.getFieldName()));
                } else {
                    this.ignoredClasses.add(target.getTargetClass());
                }
            }
        }
    }

    private void putNullable(final Set<SelectorGroup> nullableSelectorGroups) {
        for (SelectorGroup selectorGroup : nullableSelectorGroups) {
            for (Selector target : selectorGroup.getSelectors()) {
                final Class<?> targetType = ObjectUtils.defaultIfNull(target.getTargetClass(), this.rootClass);

                if (target.selectorType() == SelectorType.FIELD) {
                    final Field field = getField(targetType, target.getFieldName());
                    if (!field.getType().isPrimitive()) {
                        this.nullableFields.add(field);
                    }
                } else if (!targetType.isPrimitive()) {
                    this.nullableClasses.add(targetType);
                }
            }
        }
    }

    public Type getRootType() {
        return rootType;
    }

    public Class<T> getRootClass() {
        return rootClass;
    }

    public boolean isIgnored(final Field field) {
        return ignoredFields.contains(field);
    }

    public boolean isIgnored(final Class<?> klass) {
        return ignoredClasses.contains(klass);
    }

    public boolean isNullable(final Field field) {
        return nullableFields.contains(field);
    }

    public boolean isNullable(final Class<?> klass) {
        return nullableClasses.contains(klass);
    }

    public Optional<Generator<?>> getUserSuppliedGenerator(final Field field) {
        return Optional.ofNullable(userSuppliedFieldGenerators.get(field));
    }

    public Optional<Generator<?>> getUserSuppliedGenerator(final Class<?> klass) {
        return Optional.ofNullable(userSuppliedClassGenerators.get(klass));
    }

    public OnCompleteCallback<?> getUserSuppliedFieldCallback(final Field field) {
        return userSuppliedFieldCallbacks.get(field);
    }

    public OnCompleteCallback<?> getUserSuppliedClassCallback(final Class<?> targetClass) {
        return userSuppliedClassCallbacks.get(targetClass);
    }

    public Class<?> getSubtypeMapping(Class<?> superType) {
        return classSubtypeMap.getOrDefault(superType, superType);
    }

    public Map<Class<?>, Class<?>> getClassSubtypeMap() {
        return classSubtypeMap;
    }

    public Map<Field, Class<?>> getFieldSubtypeMap() {
        return fieldSubtypeMap;
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }

    public Settings getSettings() {
        return settings;
    }

    public Integer getSeed() {
        return seed;
    }

    public Random getRandom() {
        return random;
    }

    private static Map<TypeVariable<?>, Class<?>> buildRootTypeMap(
            final Class<?> rootClass,
            final List<Class<?>> rootTypeParameters) {

        ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);

        final Class<?> targetClass = rootClass.isArray()
                ? rootClass.getComponentType()
                : rootClass;

        final TypeVariable<?>[] typeVariables = targetClass.getTypeParameters();
        final Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();

        for (int i = 0; i < typeVariables.length; i++) {
            final TypeVariable<?> typeVariable = typeVariables[i];
            final Class<?> actualType = rootTypeParameters.get(i);
            LOG.trace("Mapping type variable '{}' to '{}'", typeVariable, actualType);
            typeMap.put(typeVariable, actualType);
        }
        return typeMap;
    }

    public Builder<T> toBuilder() {
        final Builder<T> builder = new Builder<>(rootClass, rootType);
        builder.rootTypeParameters.addAll(this.rootTypeParameters);
        builder.seed = this.seed;
        builder.settings = this.settings;
        builder.nullableSelectorGroups.addAll(this.nullableSelectorGroups);
        builder.ignoredSelectorGroups.addAll(this.ignoredSelectorGroups);
        builder.generatorSelectors.putAll(this.generatorSelectors);
        builder.generatorSpecSelectors.putAll(this.generatorSpecSelectors);
        builder.subtypeSelectors.putAll(this.subtypeSelectors);
        builder.onCompleteCallbacks.putAll(this.onCompleteCallbacks);
        return builder;
    }

    public static <T> Builder<T> builder(final Type rootType) {
        return new Builder<>(rootType);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<T> {
        private final Type rootType;
        private final Class<T> rootClass;
        private final List<Class<?>> rootTypeParameters = new ArrayList<>();
        private final Map<SelectorGroup, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecSelectors = new HashMap<>();
        private final Map<SelectorGroup, Generator<?>> generatorSelectors = new HashMap<>();
        private final Map<SelectorGroup, OnCompleteCallback<?>> onCompleteCallbacks = new HashMap<>();
        private final Set<SelectorGroup> ignoredSelectorGroups = new HashSet<>();
        private final Set<SelectorGroup> nullableSelectorGroups = new HashSet<>();
        private final Map<SelectorGroup, Class<?>> subtypeSelectors = new HashMap<>();
        private Settings settings;
        private Integer seed;

        private Builder(final Class<T> rootClass, final Type rootType) {
            this.rootClass = Verify.notNull(rootClass, "Root class is null");
            this.rootType = rootType;
        }

        private Builder(final Type rootType) {
            this.rootType = Verify.notNull(rootType, "Root type is null");
            this.rootClass = TypeUtils.getRawType(this.rootType);
        }

        public Builder<T> withRootTypeParameters(final List<Class<?>> rootTypeParameters) {
            ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);
            this.rootTypeParameters.addAll(rootTypeParameters);
            return this;
        }

        public Builder<T> withIgnored(final SelectorGroup selectorGroup) {
            this.ignoredSelectorGroups.add(selectorGroup);
            return this;
        }

        public Builder<T> withNullable(final SelectorGroup selectorGroup) {
            this.nullableSelectorGroups.add(selectorGroup);
            return this;
        }

        public Builder<T> withSubtype(final SelectorGroup selectorGroup, final Class<?> subtype) {
            this.subtypeSelectors.put(selectorGroup, subtype);
            return this;
        }

        public Builder<T> withGenerator(final SelectorGroup selectorGroup, final Generator<?> generator) {
            this.generatorSelectors.put(selectorGroup, generator);
            return this;
        }

        public Builder<T> withSupplier(final SelectorGroup selectorGroup, final Supplier<?> supplier) {
            this.generatorSelectors.put(selectorGroup, random -> supplier.get());
            return this;
        }

        public Builder<T> withGeneratorSpec(final SelectorGroup target, final Function<Generators, ? extends GeneratorSpec<?>> spec) {
            this.generatorSpecSelectors.put(target, spec);
            return this;
        }

        public <V> Builder<T> withOnCompleteCallback(final SelectorGroup target, final OnCompleteCallback<V> callback) {
            this.onCompleteCallbacks.put(target, callback);
            return this;
        }

        public Builder<T> withSettings(final Settings settings) {
            this.settings = settings;
            return this;
        }

        public Builder<T> withSeed(final int seed) {
            this.seed = seed;
            return this;
        }

        public ModelContext<T> build() {
            return new ModelContext<>(this);
        }
    }
}