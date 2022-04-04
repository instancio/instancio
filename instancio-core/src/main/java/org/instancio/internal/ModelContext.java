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

import org.instancio.Binding;
import org.instancio.Generator;
import org.instancio.GeneratorContext;
import org.instancio.GeneratorSpec;
import org.instancio.Generators;
import org.instancio.OnCompleteCallback;
import org.instancio.generators.ArrayGenerator;
import org.instancio.internal.random.RandomProvider;
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
    private final Map<Binding, OnCompleteCallback<?>> onCompleteCallbacks;
    private final Map<Class<?>, Class<?>> subtypeMap;
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Settings settings;
    private final Integer seed;
    private final RandomProvider randomProvider;
    private final Set<Binding> ignoredBindings;
    private final Set<Binding> nullableBindings;
    private final Map<Binding, Generator<?>> generatorBindings;
    private final Map<Binding, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecBindings;

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
        this.ignoredBindings = Collections.unmodifiableSet(builder.ignoredBindings);
        this.nullableBindings = Collections.unmodifiableSet(builder.nullableBindings);
        this.generatorBindings = Collections.unmodifiableMap(builder.generatorBindings);
        this.generatorSpecBindings = Collections.unmodifiableMap(builder.generatorSpecBindings);
        this.onCompleteCallbacks = Collections.unmodifiableMap(builder.onCompleteCallbacks);
        this.subtypeMap = Collections.unmodifiableMap(builder.subtypeMap);

        this.rootTypeMap = rootType instanceof ParameterizedType || rootType instanceof GenericArrayType
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(buildRootTypeMap(rootClass, builder.rootTypeParameters));

        this.settings = Settings.defaults()
                .merge(Settings.from(new PropertiesLoader().load("instancio.properties")))
                .merge(builder.settings)
                .lock();

        this.seed = builder.seed;
        this.randomProvider = resolveRandomProvider(seed);

        putAllCallbacks(builder.onCompleteCallbacks);
        putAllBuiltInGenerators(builder.generatorSpecBindings);
        putAllUserSuppliedGenerators(builder.generatorBindings);
        putIgnored(builder.ignoredBindings);
        putNullable(builder.nullableBindings);
    }

    private static RandomProvider resolveRandomProvider(@Nullable final Integer userSuppliedSeed) {
        if (userSuppliedSeed != null) {
            return new RandomProvider(userSuppliedSeed);
        }
        // If running under JUnit extension, use the provider supplied by the extension
        return ObjectUtils.defaultIfNull(
                ThreadLocalRandomProvider.getInstance().get(),
                () -> new RandomProvider(SeedUtil.randomSeed()));
    }

    private void putAllCallbacks(final Map<Binding, OnCompleteCallback<?>> onCompleteCallbacks) {
        onCompleteCallbacks.forEach(this::putCallbackBinding);
    }

    private void putAllBuiltInGenerators(final Map<Binding, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecBindings) {
        final GeneratorContext generatorContext = new GeneratorContext(settings, randomProvider);
        final Generators generators = new Generators(generatorContext);
        generatorSpecBindings.forEach((target, genSpecFn) -> {
            final Generator<?> generator = (Generator<?>) genSpecFn.apply(generators);
            putGeneratorBinding(target, generator);
        });
    }

    private void putAllUserSuppliedGenerators(final Map<Binding, Generator<?>> generatorBindings) {
        generatorBindings.forEach(this::putGeneratorBinding);
    }

    private void putCallbackBinding(final Binding binding, final OnCompleteCallback<?> callback) {
        for (Binding.BindingTarget target : binding.getTargets()) {
            if (target.isFieldBinding()) {
                final Class<?> targetType = defaultIfNull(target.getTargetType(), this.rootClass);
                final Field field = getField(targetType, target.getFieldName());
                this.userSuppliedFieldCallbacks.put(field, callback);
            } else {
                this.userSuppliedClassCallbacks.put(target.getTargetType(), callback);
            }
        }
    }

    private void putGeneratorBinding(final Binding binding, final Generator<?> generator) {
        for (Binding.BindingTarget target : binding.getTargets()) {
            if (target.isFieldBinding()) {
                final Class<?> targetType = defaultIfNull(target.getTargetType(), this.rootClass);
                final Field field = getField(targetType, target.getFieldName());

                // TODO refactor to remove the isArray conditional
                if (field.getType().isArray() && generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).type(field.getType());
                }
                this.userSuppliedFieldGenerators.put(field, generator);
            } else {
                if (target.getTargetType().isArray() && generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).type(target.getTargetType());
                }
                this.userSuppliedClassGenerators.put(target.getTargetType(), generator);
            }
        }
    }

    private void putIgnored(Set<Binding> ignoredBindings) {
        for (Binding binding : ignoredBindings) {
            for (Binding.BindingTarget target : binding.getTargets()) {
                if (target.isFieldBinding()) {
                    final Class<?> targetType = ObjectUtils.defaultIfNull(target.getTargetType(), this.rootClass);
                    this.ignoredFields.add(getField(targetType, target.getFieldName()));
                } else {
                    this.ignoredClasses.add(target.getTargetType());
                }
            }
        }
    }

    private void putNullable(final Set<Binding> nullableBindings) {
        for (Binding binding : nullableBindings) {
            for (Binding.BindingTarget target : binding.getTargets()) {
                final Class<?> targetType = ObjectUtils.defaultIfNull(target.getTargetType(), this.rootClass);

                if (target.isFieldBinding()) {
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
        return subtypeMap.getOrDefault(superType, superType);
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

    public RandomProvider getRandomProvider() {
        return randomProvider;
    }

    private static Map<TypeVariable<?>, Class<?>> buildRootTypeMap(
            final Class<?> rootClass,
            final List<Class<?>> rootTypeParameters) {

        InstancioValidator.validateTypeParameters(rootClass, rootTypeParameters);

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
        builder.nullableBindings.addAll(this.nullableBindings);
        builder.ignoredBindings.addAll(this.ignoredBindings);
        builder.generatorBindings.putAll(this.generatorBindings);
        builder.generatorSpecBindings.putAll(this.generatorSpecBindings);
        builder.onCompleteCallbacks.putAll(this.onCompleteCallbacks);
        builder.subtypeMap.putAll(this.subtypeMap);
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
        private final Map<Class<?>, Class<?>> subtypeMap = new HashMap<>();
        private final Map<Binding, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecBindings = new HashMap<>();
        private final Map<Binding, OnCompleteCallback<?>> onCompleteCallbacks = new HashMap<>();
        private final Set<Binding> ignoredBindings = new HashSet<>();
        private final Set<Binding> nullableBindings = new HashSet<>();
        private final Map<Binding, Generator<?>> generatorBindings = new HashMap<>();
        private Settings settings;
        private Integer seed;

        private Builder(final Class<T> rootClass, final Type rootType) {
            this.rootClass = rootClass;
            this.rootType = rootType;
        }

        private Builder(final Type rootType) {
            this.rootType = Verify.notNull(rootType, "Root type is null");
            this.rootClass = TypeUtils.getRawType(this.rootType);
        }

        public Builder<T> withRootTypeParameters(final List<Class<?>> rootTypeParameters) {
            InstancioValidator.validateTypeParameters(rootClass, rootTypeParameters);
            this.rootTypeParameters.addAll(rootTypeParameters);
            return this;
        }

        public Builder<T> withIgnored(final Binding binding) {
            this.ignoredBindings.add(binding);
            return this;
        }

        public Builder<T> withNullable(final Binding binding) {
            this.nullableBindings.add(binding);
            return this;
        }

        public Builder<T> withGenerator(final Binding binding, final Generator<?> generator) {
            this.generatorBindings.put(binding, generator);
            return this;
        }

        public Builder<T> withGeneratorSpec(final Binding target, final Function<Generators, ? extends GeneratorSpec<?>> spec) {
            this.generatorSpecBindings.put(target, spec);
            return this;
        }

        public <V> Builder<T> withOnCompleteCallback(final Binding target, final OnCompleteCallback<V> callback) {
            this.onCompleteCallbacks.put(target, callback);
            return this;
        }

        public Builder<T> withSubtypeMapping(final Class<?> from, final Class<?> to) {
            InstancioValidator.validateSubtypeMapping(from, to);
            this.subtypeMap.put(from, to);
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
