/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.Assignment;
import org.instancio.GeneratorSpecProvider;
import org.instancio.OnCompleteCallback;
import org.instancio.Random;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;
import org.instancio.internal.RandomHelper;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.InternalContainerFactoryProvider;
import org.instancio.internal.spi.InternalServiceProviderContext;
import org.instancio.internal.spi.Providers;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ServiceLoaders;
import org.instancio.internal.util.Sonar;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.function.Supplier;

import static org.instancio.internal.context.ModelContextHelper.buildRootTypeMap;
import static org.instancio.internal.context.ModelContextHelper.preProcess;

@SuppressWarnings("PMD.ExcessiveImports")
public final class ModelContext<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ModelContext.class);

    private static final List<InternalContainerFactoryProvider> CONTAINER_FACTORIES =
            CollectionUtils.combine(
                    ServiceLoaders.loadAll(InternalContainerFactoryProvider.class),
                    new InternalContainerFactoryProviderImpl());


    private final Providers providers;
    private final Type rootType;
    private final List<Type> rootTypeParameters;
    private final Map<TypeVariable<?>, Type> rootTypeMap;
    private final Integer maxDepth;
    private final Long seed;
    private final Random random;
    private final Settings settings;
    private final BooleanSelectorMap ignoredSelectorMap;
    private final BooleanSelectorMap nullableSelectorMap;
    private final OnCompleteCallbackSelectorMap onCompleteCallbackSelectorMap;
    private final SubtypeSelectorMap subtypeSelectorMap;
    private final GeneratorSelectorMap generatorSelectorMap;
    private final AssignmentSelectorMap assignmentSelectorMap;

    private ModelContext(final Builder<T> builder) {
        rootType = builder.rootType;
        rootTypeParameters = Collections.unmodifiableList(builder.rootTypeParameters);

        rootTypeMap = rootType instanceof ParameterizedType || rootType instanceof GenericArrayType
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(buildRootTypeMap(builder.rootType, builder.rootTypeParameters));

        seed = builder.seed;
        maxDepth = builder.maxDepth;
        settings = createSettings(builder);

        random = RandomHelper.resolveRandom(settings.get(Keys.SEED), builder.seed);

        ignoredSelectorMap = new BooleanSelectorMap(builder.ignoredTargets);
        nullableSelectorMap = new BooleanSelectorMap(builder.nullableTargets);
        onCompleteCallbackSelectorMap = new OnCompleteCallbackSelectorMap(builder.onCompleteCallbacks);
        subtypeSelectorMap = new SubtypeSelectorMap(builder.subtypeSelectors);

        final GeneratorContext generatorContext = new GeneratorContext(settings, random);

        generatorSelectorMap = new GeneratorSelectorMap(
                generatorContext,
                builder.generatorSelectors,
                builder.generatorSpecSelectors);

        subtypeSelectorMap.putAll(generatorSelectorMap.getGeneratorSubtypeMap());
        assignmentSelectorMap = new AssignmentSelectorMap(builder.assignmentSelectors, generatorContext);

        providers = new Providers(
                ServiceLoaders.loadAll(InstancioServiceProvider.class),
                new InternalServiceProviderContext(settings, random));
    }

    private static Settings createSettings(final Builder<?> builder) {
        final Settings settings = Global.getPropertiesFileSettings()
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings);

        if (Boolean.TRUE.equals(builder.lenient)) {
            settings.set(Keys.MODE, Mode.LENIENT);
        }
        LOG.trace("Resolved settings: {}", settings);
        return settings.lock();
    }

    public List<InternalContainerFactoryProvider> getContainerFactories() {
        return CONTAINER_FACTORIES;
    }

    public Providers getServiceProviders() {
        return providers;
    }

    public void reportWarnings() {
        reportUnusedSelectorWarnings();
        reportEmitGeneratorWarnings();
    }

    private void reportEmitGeneratorWarnings() {
        final SelectorMap<Generator<?>> selectorMap = generatorSelectorMap.getSelectorMap();
        final UnusedEmitItemsReporter reporter = new UnusedEmitItemsReporter(selectorMap);
        reporter.report();
    }

    void reportUnusedSelectorWarnings() {
        if (settings.get(Keys.MODE) == Mode.STRICT) {
            final UnusedSelectorReporter reporter = UnusedSelectorReporter.builder()
                    .maxDepth(getMaxDepth())
                    .ignored(ignoredSelectorMap.getSelectorMap().getUnusedKeys())
                    .nullable(nullableSelectorMap.getSelectorMap().getUnusedKeys())
                    .generators(generatorSelectorMap.getSelectorMap().getUnusedKeys())
                    .callbacks(onCompleteCallbackSelectorMap.getSelectorMap().getUnusedKeys())
                    .subtypes(subtypeSelectorMap.getSelectorMap().getUnusedKeys())
                    // confusing naming (origin selectors are the keys, which is what we need)
                    .assignmentOrigins(assignmentSelectorMap.getDestinationSelectors().getSelectorMap().getUnusedKeys())
                    .assignmentDestinations(assignmentSelectorMap.getSelectorMap().getUnusedKeys())
                    .build();

            reporter.report();
        }
    }

    public Type getRootType() {
        return rootType;
    }

    public Integer getMaxDepth() {
        return ObjectUtils.defaultIfNull(maxDepth, settings.get(Keys.MAX_DEPTH));
    }

    public boolean isIgnored(final InternalNode node) {
        return ignoredSelectorMap.isTrue(node);
    }

    public boolean isNullable(final InternalNode node) {
        return nullableSelectorMap.isTrue(node);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Optional<Generator<?>> getGenerator(final InternalNode node) {
        return generatorSelectorMap.getGenerator(node);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public List<OnCompleteCallback<?>> getCallbacks(final InternalNode node) {
        return onCompleteCallbackSelectorMap.getCallbacks(node);
    }

    public BooleanSelectorMap getIgnoredSelectorMap() {
        return ignoredSelectorMap;
    }

    public SubtypeSelectorMap getSubtypeSelectorMap() {
        return subtypeSelectorMap;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Map<TypeVariable<?>, Type> getRootTypeMap() {
        return rootTypeMap;
    }

    public Settings getSettings() {
        return settings;
    }

    public Random getRandom() {
        return random;
    }

    public Builder<T> toBuilder() {
        final Builder<T> builder = new Builder<>(rootType);
        builder.rootTypeParameters.addAll(this.rootTypeParameters);
        builder.maxDepth = this.maxDepth;
        builder.seed = this.seed;
        builder.settings = this.settings;
        builder.nullableTargets.addAll(this.nullableSelectorMap.getTargetSelectors());
        builder.ignoredTargets.addAll(this.ignoredSelectorMap.getTargetSelectors());
        builder.generatorSelectors.putAll(this.generatorSelectorMap.getGeneratorSelectors());
        builder.generatorSpecSelectors.putAll(this.generatorSelectorMap.getGeneratorSpecSelectors());
        builder.subtypeSelectors.putAll(this.subtypeSelectorMap.getSubtypeSelectors());
        builder.onCompleteCallbacks.putAll(this.onCompleteCallbackSelectorMap.getOnCompleteCallbackSelectors());
        builder.assignmentSelectors.putAll(this.assignmentSelectorMap.getAssignmentSelectors());
        return builder;
    }

    public static <T> Builder<T> builder(final Type rootType) {
        return new Builder<>(rootType);
    }

    public List<InternalAssignment> getAssignments(final InternalNode node) {
        return assignmentSelectorMap.getAssignments(node);
    }

    public BooleanSelectorMap getAssignmentOriginSelectorMap() {
        return assignmentSelectorMap.getOriginSelectors();
    }

    public List<TargetSelector> getAssignmentDestinationSelectors(final InternalNode node) {
        return assignmentSelectorMap.getDestinationSelectors(node);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<T> {
        private final Type rootType;
        private final Class<T> rootClass;

        private final List<Type> rootTypeParameters = new ArrayList<>();
        private final Map<TargetSelector, Class<?>> subtypeSelectors = new LinkedHashMap<>();
        private final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecSelectors = new LinkedHashMap<>();
        private final Map<TargetSelector, Generator<?>> generatorSelectors = new LinkedHashMap<>();
        private final Map<TargetSelector, OnCompleteCallback<?>> onCompleteCallbacks = new LinkedHashMap<>();
        private final Map<TargetSelector, List<Assignment>> assignmentSelectors = new LinkedHashMap<>();
        private final Set<TargetSelector> ignoredTargets = new LinkedHashSet<>();
        private final Set<TargetSelector> nullableTargets = new LinkedHashSet<>();
        private Settings settings;
        private Integer maxDepth;
        private Long seed;
        private Boolean lenient;

        private Builder(final Type rootType) {
            this.rootType = Verify.notNull(rootType, "Root type is null");
            this.rootClass = TypeUtils.getRawType(this.rootType);
        }

        public Builder<T> withRootTypeParameters(final List<Type> rootTypeParameters) {
            ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);
            this.rootTypeParameters.addAll(rootTypeParameters);
            return this;
        }

        public Builder<T> withSubtype(final TargetSelector selector, final Class<?> subtype) {
            this.subtypeSelectors.put(preProcess(selector, rootClass),
                    ApiValidator.notNull(subtype, "subtype must not be null"));
            return this;
        }

        public Builder<T> withGenerator(final TargetSelector selector, final Generator<?> generator) {
            this.generatorSelectors.put(preProcess(selector, rootClass), generator);
            return this;
        }

        public Builder<T> withSupplier(final TargetSelector selector, final Supplier<?> supplier) {
            this.generatorSelectors.put(preProcess(selector, rootClass), GeneratorDecorator.decorate(supplier));
            return this;
        }

        public <V> Builder<T> withGeneratorSpec(final TargetSelector selector, final GeneratorSpecProvider<V> spec) {
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

        public Builder<T> withMaxDepth(final int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder<T> withNullable(final TargetSelector selector) {
            this.nullableTargets.add(preProcess(selector, rootClass));
            return this;
        }

        public Builder<T> withAssignments(final Assignment... assignments) {
            for (Assignment assignment : assignments) {
                ApiValidator.notNull(assignment, "assignments array must not contain null");
                processAssignment(assignment);
            }
            return this;
        }

        private void processAssignment(final Assignment assignment) {
            final List<InternalAssignment> assignments = ((Flattener<InternalAssignment>) assignment).flatten();

            for (InternalAssignment c : assignments) {
                final TargetSelector origin = preProcess(c.getOrigin(), rootClass);
                final TargetSelector destination = preProcess(c.getDestination(), rootClass);

                final Assignment processedAssignment = c.toBuilder()
                        .origin(origin)
                        .destination(destination)
                        .build();

                this.assignmentSelectors
                        .computeIfAbsent(destination, k -> new ArrayList<>())
                        .add(processedAssignment);
            }
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

        public Builder<T> useModelAsTypeArgument(final ModelContext<?> otherContext) {
            rootTypeParameters.add(otherContext.getRootType());
            seed = otherContext.seed;
            nullableTargets.addAll(otherContext.nullableSelectorMap.getTargetSelectors());
            ignoredTargets.addAll(otherContext.ignoredSelectorMap.getTargetSelectors());
            generatorSelectors.putAll(otherContext.generatorSelectorMap.getGeneratorSelectors());
            generatorSpecSelectors.putAll(otherContext.generatorSelectorMap.getGeneratorSpecSelectors());
            subtypeSelectors.putAll(otherContext.subtypeSelectorMap.getSubtypeSelectors());
            onCompleteCallbacks.putAll(otherContext.onCompleteCallbackSelectorMap.getOnCompleteCallbackSelectors());
            assignmentSelectors.putAll(otherContext.assignmentSelectorMap.getAssignmentSelectors());

            // Increment max depth to account for the additional layer added by the collection
            maxDepth = otherContext.maxDepth == null ? null : otherContext.maxDepth + 1;
            settings = Settings.from(otherContext.settings)
                    .set(Keys.MAX_DEPTH, otherContext.settings.get(Keys.MAX_DEPTH) + 1)
                    .lock();

            return this;
        }

        public ModelContext<T> build() {
            return new ModelContext<>(this);
        }
    }
}