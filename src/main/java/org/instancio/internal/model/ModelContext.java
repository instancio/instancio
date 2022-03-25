package org.instancio.internal.model;

import org.instancio.Binding;
import org.instancio.Generator;
import org.instancio.GeneratorSpec;
import org.instancio.Generators;
import org.instancio.exception.InstancioApiException;
import org.instancio.settings.Settings;
import org.instancio.generators.ArrayGenerator;
import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.PropertiesLoader;
import org.instancio.util.TypeUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
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
    private final Map<Class<?>, Class<?>> subtypeMap;
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Settings settings;
    private final Integer seed;

    private ModelContext(final Builder<T> builder) {
        putAllBuiltInGenerators(builder);

        this.rootType = builder.rootType;
        this.rootClass = builder.rootClass;
        this.rootTypeParameters = Collections.unmodifiableList(builder.rootTypeParameters);
        this.ignoredFields = Collections.unmodifiableSet(builder.ignoredFields);
        this.nullableFields = Collections.unmodifiableSet(builder.nullableFields);
        this.ignoredClasses = Collections.unmodifiableSet(builder.ignoredClasses);
        this.nullableClasses = Collections.unmodifiableSet(builder.nullableClasses);
        this.userSuppliedFieldGenerators = Collections.unmodifiableMap(builder.userSuppliedFieldGenerators);
        this.userSuppliedClassGenerators = Collections.unmodifiableMap(builder.userSuppliedClassGenerators);
        this.subtypeMap = Collections.unmodifiableMap(builder.subtypeMap);
        this.rootTypeMap = rootType instanceof ParameterizedType
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(buildRootTypeMap(rootClass, builder.rootTypeParameters));
        this.settings = mergeDefaultSettingsWith(builder.userSuppliedSettings);
        this.seed = builder.seed;
    }

    private Settings mergeDefaultSettingsWith(final Settings userSuppliedSettings) {
        return Settings.defaults()
                .merge(Settings.from(new PropertiesLoader().load("instancio.properties")))
                .merge(userSuppliedSettings)
                .lock();
    }

    private static <T> void putAllBuiltInGenerators(final Builder<T> builder) {
        final int seed = defaultIfNull(builder.seed, ThreadLocalRandom.current().nextInt());
        final RandomProvider random = new RandomProvider(seed);
        final Generators generators = new Generators(random);

        builder.generatorSpecMap.forEach((target, genSpecFn) -> {
            final Generator<?> generator = (Generator<?>) genSpecFn.apply(generators);
            builder.withGenerator(target, generator);
        });
    }

    public Builder<T> toBuilder() {
        final Builder<T> builder = new Builder<>(rootClass, rootType);
        builder.rootTypeParameters.addAll(this.rootTypeParameters);
        builder.ignoredFields.addAll(this.ignoredFields);
        builder.nullableFields.addAll(this.nullableFields);
        builder.ignoredClasses.addAll(this.ignoredClasses);
        builder.nullableClasses.addAll(this.nullableClasses);
        builder.userSuppliedFieldGenerators.putAll(this.userSuppliedFieldGenerators);
        builder.userSuppliedClassGenerators.putAll(this.userSuppliedClassGenerators);
        builder.subtypeMap.putAll(this.getSubtypeMap());
        return builder;
    }

    public Type getRootType() {
        return rootType;
    }

    public Class<T> getRootClass() {
        return rootClass;
    }

    public Set<Field> getIgnoredFields() {
        return ignoredFields;
    }

    public Set<Field> getNullableFields() {
        return nullableFields;
    }

    public Set<Class<?>> getIgnoredClasses() {
        return ignoredClasses;
    }

    public Set<Class<?>> getNullableClasses() { // XXX unused except in test... ?
        return nullableClasses;
    }

    public Map<Field, Generator<?>> getUserSuppliedFieldGenerators() {
        return userSuppliedFieldGenerators;
    }

    public Map<Class<?>, Generator<?>> getUserSuppliedClassGenerators() {
        return userSuppliedClassGenerators;
    }

    public Map<Class<?>, Class<?>> getSubtypeMap() {
        return subtypeMap;
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

    private static Map<TypeVariable<?>, Class<?>> buildRootTypeMap(
            final Class<?> rootClass,
            final List<Class<?>> rootTypeParameters) {

        InstancioValidator.validateTypeParameters(rootClass, rootTypeParameters);

        final TypeVariable<?>[] typeVariables = rootClass.getTypeParameters();
        final Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();

        for (int i = 0; i < typeVariables.length; i++) {
            final TypeVariable<?> typeVariable = typeVariables[i];
            final Class<?> actualType = rootTypeParameters.get(i);
            LOG.trace("Mapping type variable '{}' to {}", typeVariable, actualType);
            typeMap.put(typeVariable, actualType);
        }
        return typeMap;
    }

    public static <T> Builder<T> builder(final Type rootType) {
        return new Builder<>(rootType);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<T> {
        private final Type rootType;
        private final Class<T> rootClass;
        private final List<Class<?>> rootTypeParameters = new ArrayList<>();
        private final Set<Field> ignoredFields = new HashSet<>();
        private final Set<Field> nullableFields = new HashSet<>();
        private final Set<Class<?>> ignoredClasses = new HashSet<>();
        private final Set<Class<?>> nullableClasses = new HashSet<>();
        private final Map<Field, Generator<?>> userSuppliedFieldGenerators = new HashMap<>();
        private final Map<Class<?>, Generator<?>> userSuppliedClassGenerators = new HashMap<>();
        private final Map<Class<?>, Class<?>> subtypeMap = new HashMap<>();
        private final Map<Binding, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecMap = new HashMap<>();
        private Settings userSuppliedSettings;
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

        public Builder<T> withIgnoredField(final Field field) {
            this.ignoredFields.add(field);
            return this;
        }

        public Builder<T> withNullableField(final Field field) {
            if (field.getType().isPrimitive()) {
                throw new InstancioApiException(String.format("Primitive field '%s' cannot be set to null", field));
            }
            this.nullableFields.add(field);
            return this;
        }

        public Builder<T> withIgnoredClass(final Class<?> klass) {
            this.ignoredClasses.add(klass);
            return this;
        }

        public Builder<T> withNullableClass(final Class<?> klass) {
            if (klass.isPrimitive()) {
                throw new InstancioApiException(String.format("Primitive class '%s' cannot be set to null", klass.getName()));
            }
            this.nullableClasses.add(klass);
            return this;
        }

        public Builder<T> withGenerator(final Binding target, final Generator<?> generator) {
            if (target.isFieldBinding()) {
                final Class<?> targetType = defaultIfNull(target.getTargetType(), this.rootClass);
                final Field field = getField(targetType, target.getFieldName());

                if (field.getType().isArray() && generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).type(field.getType().getComponentType());
                }
                this.userSuppliedFieldGenerators.put(field, generator);
            } else {
                if (target.getTargetType().isArray() && generator instanceof ArrayGenerator) {
                    ((ArrayGenerator<?>) generator).type(target.getTargetType().getComponentType());
                }
                this.userSuppliedClassGenerators.put(target.getTargetType(), generator);

                // e.g. if int.class, map Integer.class to the same generator (and vice versa)
                PrimitiveWrapperBiLookup.getEquivalent(target.getTargetType())
                        .ifPresent(equivalent -> this.userSuppliedClassGenerators.put(equivalent, generator));
            }
            return this;
        }

        public Builder<T> withGeneratorSpec(final Binding target, final Function<Generators, ? extends GeneratorSpec<?>> spec) {
            this.generatorSpecMap.put(target, spec);
            return this;
        }

        public Builder<T> withSubtypeMapping(final Class<?> from, final Class<?> to) {
            InstancioValidator.validateSubtypeMapping(from, to);
            this.subtypeMap.put(from, to);
            return this;
        }

        public Builder<T> withSettings(final Settings settings) {
            this.userSuppliedSettings = settings;
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
