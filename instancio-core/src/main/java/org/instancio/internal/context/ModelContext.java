/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.context;

import org.instancio.Mode;
import org.instancio.OnCompleteCallback;
import org.instancio.Random;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.ThreadLocalRandom;
import org.instancio.internal.ThreadLocalSettings;
import org.instancio.internal.generator.misc.SupplierAdapter;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.internal.random.Seeds;
import org.instancio.internal.spi.InternalContainerFactoryProvider;
import org.instancio.internal.util.ServiceLoaders;
import org.instancio.internal.util.Sonar;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.instancio.internal.context.ModelContextHelper.buildRootTypeMap;
import static org.instancio.internal.context.ModelContextHelper.preProcess;

@SuppressWarnings("PMD.ExcessiveImports")
public final class ModelContext<T> {

    private static final Settings PROPERTIES_FILE_SETTINGS =
            Settings.from(PropertiesLoader.loadDefaultPropertiesFile()).lock();

    private static final Random GLOBAL_RANDOM = PROPERTIES_FILE_SETTINGS.get(Keys.SEED) == null
            ? null : new DefaultRandom(PROPERTIES_FILE_SETTINGS.get(Keys.SEED));

    private static final List<InternalContainerFactoryProvider> CONTAINER_FACTORIES = ServiceLoaders.loadAll(InternalContainerFactoryProvider.class);

    private final Type rootType;
    private final List<Class<?>> rootTypeParameters;
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Long seed;
    private final Random random;
    private final Settings settings;
    private final BooleanSelectorMap ignoredSelectorMap;
    private final BooleanSelectorMap nullableSelectorMap;
    private final OnCompleteCallbackSelectorMap onCompleteCallbackSelectorMap;
    private final SubtypeSelectorMap subtypeSelectorMap;
    private final GeneratorSelectorMap generatorSelectorMap;

    private ModelContext(final Builder<T> builder) {
        rootType = builder.rootType;
        rootTypeParameters = Collections.unmodifiableList(builder.rootTypeParameters);
        rootTypeMap = rootType instanceof ParameterizedType || rootType instanceof GenericArrayType
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(buildRootTypeMap(builder.rootType, builder.rootTypeParameters));

        seed = builder.seed;
        settings = createSettings(builder);
        random = resolveRandom(settings, builder.seed);

        ignoredSelectorMap = new BooleanSelectorMap(builder.ignoredTargets);
        nullableSelectorMap = new BooleanSelectorMap(builder.nullableTargets);
        onCompleteCallbackSelectorMap = new OnCompleteCallbackSelectorMap(builder.onCompleteCallbacks);
        subtypeSelectorMap = new SubtypeSelectorMap(builder.subtypeSelectors);
        generatorSelectorMap = new GeneratorSelectorMap(
                new GeneratorContext(settings, random),
                builder.generatorSelectors,
                builder.generatorSpecSelectors);

        subtypeSelectorMap.putAll(generatorSelectorMap.getGeneratorSubtypeMap());
    }

    private static Settings createSettings(final Builder<?> builder) {
        final Settings settings = Settings.defaults()
                .merge(PROPERTIES_FILE_SETTINGS)
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings);

        if (Boolean.TRUE.equals(builder.lenient)) {
            settings.set(Keys.MODE, Mode.LENIENT);
        }
        return settings.lock();
    }

    public List<InternalContainerFactoryProvider> getContainerFactories() {
        return CONTAINER_FACTORIES;
    }

    public void reportUnusedSelectorWarnings() {
        if (settings.get(Keys.MODE) == Mode.STRICT) {
            final UnusedSelectorReporter reporter = UnusedSelectorReporter.builder()
                    .ignored(ignoredSelectorMap.getSelectorMap().getUnusedKeys())
                    .nullable(nullableSelectorMap.getSelectorMap().getUnusedKeys())
                    .generators(generatorSelectorMap.getSelectorMap().getUnusedKeys())
                    .callbacks(onCompleteCallbackSelectorMap.getSelectorMap().getUnusedKeys())
                    .subtypes(subtypeSelectorMap.getSelectorMap().getUnusedKeys())
                    .build();

            reporter.report();
        }
    }

    private static Random resolveRandom(final Settings settings, @Nullable final Long userSuppliedSeed) {
        if (userSuppliedSeed != null) {
            return new DefaultRandom(userSuppliedSeed);
        }

        final Long settingsSeed = settings.get(Keys.SEED);
        if (settingsSeed != null && (getGlobalRandom() == null || getGlobalRandom().getSeed() != settingsSeed)) {
            // Use seed from settings unless it's the value from the properties file.
            return new DefaultRandom(settingsSeed);
        }

        // If running under JUnit extension, use the Random instance supplied by the extension
        if (ThreadLocalRandom.getInstance().get() != null) {
            return ThreadLocalRandom.getInstance().get();
        }
        if (getGlobalRandom() != null) {
            return getGlobalRandom();
        }

        return new DefaultRandom(Seeds.randomSeed());
    }

    public Type getRootType() {
        return rootType;
    }

    public boolean isIgnored(final Node node) {
        return ignoredSelectorMap.isTrue(node);
    }

    public boolean isNullable(final Node node) {
        return nullableSelectorMap.isTrue(node);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Optional<Generator<?>> getGenerator(final Node node) {
        return generatorSelectorMap.getGenerator(node);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public List<OnCompleteCallback<?>> getCallbacks(final Node node) {
        return onCompleteCallbackSelectorMap.getCallbacks(node);
    }

    public SubtypeSelectorMap getSubtypeSelectorMap() {
        return subtypeSelectorMap;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }

    public Settings getSettings() {
        return settings;
    }

    public Random getRandom() {
        return random;
    }

    public static Random getGlobalRandom() {
        return GLOBAL_RANDOM;
    }

    public Builder<T> toBuilder() {
        final Builder<T> builder = new Builder<>(rootType);
        builder.rootTypeParameters.addAll(this.rootTypeParameters);
        builder.seed = this.seed;
        builder.settings = this.settings;
        builder.nullableTargets.addAll(this.nullableSelectorMap.getTargetSelectors());
        builder.ignoredTargets.addAll(this.ignoredSelectorMap.getTargetSelectors());
        builder.generatorSelectors.putAll(this.generatorSelectorMap.getGeneratorSelectors());
        builder.generatorSpecSelectors.putAll(this.generatorSelectorMap.getGeneratorSpecSelectors());
        builder.subtypeSelectors.putAll(this.subtypeSelectorMap.getSubtypeSelectors());
        builder.onCompleteCallbacks.putAll(this.onCompleteCallbackSelectorMap.getOnCompleteCallbackSelectors());
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
        private final Map<TargetSelector, Class<?>> subtypeSelectors = new LinkedHashMap<>();
        private final Map<TargetSelector, Function<Generators, ? extends GeneratorSpec<?>>> generatorSpecSelectors = new LinkedHashMap<>();
        private final Map<TargetSelector, Generator<?>> generatorSelectors = new LinkedHashMap<>();
        private final Map<TargetSelector, OnCompleteCallback<?>> onCompleteCallbacks = new LinkedHashMap<>();
        private final Set<TargetSelector> ignoredTargets = new LinkedHashSet<>();
        private final Set<TargetSelector> nullableTargets = new LinkedHashSet<>();
        private Settings settings;
        private Long seed;
        private Boolean lenient;

        private Builder(final Type rootType) {
            this.rootType = Verify.notNull(rootType, "Root type is null");
            this.rootClass = TypeUtils.getRawType(this.rootType);
        }

        public Builder<T> withRootTypeParameters(final List<Class<?>> rootTypeParameters) {
            ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);
            this.rootTypeParameters.addAll(rootTypeParameters);
            return this;
        }

        public Builder<T> withSubtype(final TargetSelector selector, final Class<?> subtype) {
            this.subtypeSelectors.put(preProcess(selector, rootClass),
                    ApiValidator.notNull(subtype, "Subtype must not be null"));
            return this;
        }

        public Builder<T> withGenerator(final TargetSelector selector, final Generator<?> generator) {
            this.generatorSelectors.put(preProcess(selector, rootClass), generator);
            return this;
        }

        public Builder<T> withSupplier(final TargetSelector selector, final Supplier<?> supplier) {
            this.generatorSelectors.put(preProcess(selector, rootClass), new SupplierAdapter(supplier));
            return this;
        }

        public Builder<T> withGeneratorSpec(final TargetSelector selector, final Function<Generators, ? extends GeneratorSpec<?>> spec) {
            this.generatorSpecSelectors.put(preProcess(selector, rootClass), spec);
            return this;
        }

        public Builder<T> withOnCompleteCallback(final TargetSelector selector, final OnCompleteCallback<?> callback) {
            this.onCompleteCallbacks.put(preProcess(selector, rootClass), callback);
            return this;
        }

        public Builder<T> withIgnored(final TargetSelector selector) {
            this.ignoredTargets.add(preProcess(selector, rootClass));
            return this;
        }

        public Builder<T> withNullable(final TargetSelector selector) {
            this.nullableTargets.add(preProcess(selector, rootClass));
            return this;
        }

        public Builder<T> withSettings(final Settings arg) {
            ApiValidator.notNull(arg, "Null Settings provided to withSettings() method");

            if (settings == null) {
                settings = Settings.from(arg);
            } else {
                settings = settings.merge(arg);
            }
            return this;
        }

        public Builder<T> withSeed(final long seed) {
            this.seed = seed;
            return this;
        }

        public Builder<T> lenient() {
            this.lenient = true;
            return this;
        }

        public ModelContext<T> build() {
            return new ModelContext<>(this);
        }
    }
}