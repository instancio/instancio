/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.Model;
import org.instancio.OnCompleteCallback;
import org.instancio.Random;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;
import org.instancio.internal.InternalModel;
import org.instancio.internal.RandomHelper;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.SelectorProcessor;
import org.instancio.internal.selectors.SetterSelectorHolder;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProviderContext;
import org.instancio.internal.spi.InternalServiceProviderImpl;
import org.instancio.internal.spi.Providers;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ServiceLoaders;
import org.instancio.internal.util.Sonar;
import org.instancio.internal.util.SystemProperties;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;
import org.instancio.settings.SettingKey;
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
import static org.instancio.internal.context.SetModelSelectorHelper.applyModelSelectorScopes;
import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
public final class ModelContext<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ModelContext.class);

    private static final List<InternalServiceProvider> INTERNAL_SERVICE_PROVIDERS =
            CollectionUtils.combine(
                    ServiceLoaders.loadAll(InternalServiceProvider.class),
                    new InternalServiceProviderImpl());

    private final Providers providers;
    private final Type rootType;
    private final List<Type> rootTypeParameters;
    private final Map<TypeVariable<?>, Type> rootTypeMap;
    private final Integer maxDepth;
    private final Long seed;
    private final Random random;
    private final Settings settings;
    private final ModelContextSelectorMap modelContextSelectorMap = new ModelContextSelectorMap();
    private final BooleanSelectorMap ignoredSelectorMap = new BooleanSelectorMap();
    private final BooleanSelectorMap nullableSelectorMap = new BooleanSelectorMap();
    private final OnCompleteCallbackSelectorMap onCompleteCallbackSelectorMap = new OnCompleteCallbackSelectorMap();
    private final SubtypeSelectorMap subtypeSelectorMap = new SubtypeSelectorMap();
    private final GeneratorSelectorMap generatorSelectorMap;
    private final AssignmentSelectorMap assignmentSelectorMap;
    private final boolean verbose;

    private ModelContext(final Builder<T> builder) {
        rootType = builder.rootType;
        rootTypeParameters = Collections.unmodifiableList(builder.rootTypeParameters);

        rootTypeMap = rootType instanceof ParameterizedType || rootType instanceof GenericArrayType
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(buildRootTypeMap(builder.rootType, builder.rootTypeParameters));

        seed = builder.seed;
        maxDepth = builder.maxDepth;
        verbose = builder.verbose;
        settings = createSettings(builder);
        random = RandomHelper.resolveRandom(settings.get(Keys.SEED), builder.seed);
        final GeneratorContext generatorContext = new GeneratorContext(settings, random);
        generatorSelectorMap = new GeneratorSelectorMap(generatorContext);
        assignmentSelectorMap = new AssignmentSelectorMap(generatorContext);

        initSelectorMaps(builder);

        providers = new Providers(
                ServiceLoaders.loadAll(InstancioServiceProvider.class),
                new InternalServiceProviderContext(settings, random));
    }

    private void initSelectorMaps(final Builder<T> builder) {
        // Before initialising remaining selector maps, update this model
        // with selectors from other models provided via setModel()
        builder.modelContexts.forEach(this::copyToThisContext);

        modelContextSelectorMap.putAll(builder.modelContexts);
        ignoredSelectorMap.putAll(builder.ignoredTargets);
        nullableSelectorMap.putAll(builder.nullableTargets);
        onCompleteCallbackSelectorMap.putAll(builder.onCompleteCallbacks);
        generatorSelectorMap.putAllGeneratorSpecs(builder.generatorSpecSelectors);
        generatorSelectorMap.putAllGenerators(builder.generatorSelectors);
        assignmentSelectorMap.putAll(builder.assignmentSelectors);

        // subtype map must be last as it needs subtypes from assignment/generator spec subtypes
        subtypeSelectorMap.putAll(builder.subtypeSelectors);
        // subtypes from generators and assignments neeed to be added
        // directly to the SubtypeSelectorMap.selectorMap,
        // bypassing SubtypeSelectorMap.subtypeSelectors
        // TODO this needs to be cleaned up/refactored
        generatorSelectorMap.getGeneratorSubtypeMap().forEach((k, v) -> subtypeSelectorMap.getSelectorMap().put(k, v));
        assignmentSelectorMap.getGeneratorSubtypeMap().forEach((k, v) -> subtypeSelectorMap.getSelectorMap().put(k, v));
    }

    private static Settings createSettings(final Builder<?> builder) {
        final Settings settings = Global.getPropertiesFileSettings()
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings);

        if (Boolean.TRUE.equals(builder.lenient)) {
            settings.set(Keys.MODE, Mode.LENIENT);
        }

        // The system property override is used for running
        // feature-tests using both assignment types
        final AssignmentType assignmentTypeOverride = SystemProperties.getAssignmentType();
        if (assignmentTypeOverride != null) {
            settings.set(Keys.ASSIGNMENT_TYPE, assignmentTypeOverride);
        }

        LOG.trace("Resolved settings: {}", settings);

        final SetterSelectorHolder holder = builder.getSetMethodSelectorHolder();

        ApiValidator.failIfMethodSelectorIsUsedWithFieldAssignment(
                settings.get(Keys.ASSIGNMENT_TYPE),
                holder.getSetterSelector());

        return settings.lock();
    }

    public List<InternalServiceProvider> getInternalServiceProviders() {
        return INTERNAL_SERVICE_PROVIDERS;
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
                    .modelContexts(modelContextSelectorMap.getSelectorMap().getUnusedKeys())
                    .build();

            reporter.report();
        }
    }

    public Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> getSelectors(final InternalNode rootNode) {
        final SelectorNodeMatchesCollector collector = SelectorNodeMatchesCollector.builder()
                .ignoredSelectorMap(ignoredSelectorMap.getSelectorMap())
                .nullableSelectorMap(nullableSelectorMap.getSelectorMap())
                .onCompleteCallbackSelectorMap(onCompleteCallbackSelectorMap.getSelectorMap())
                .subtypeSelectorMap(subtypeSelectorMap.getSelectorMap())
                .generatorSelectorMap(generatorSelectorMap.getSelectorMap())
                .assignmentOriginSelectorMap(assignmentSelectorMap.getOriginSelectors().getSelectorMap())
                .assignmentDestinationSelectorMap(assignmentSelectorMap.getDestinationSelectors().getSelectorMap())
                .build();

        return collector.getNodeMatches(rootNode);
    }


    public Type getRootType() {
        return rootType;
    }

    public Integer getMaxDepth() {
        return defaultIfNull(maxDepth, settings.get(Keys.MAX_DEPTH));
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

    public ModelContextSelectorMap getModelContextSelectorMap() {
        return modelContextSelectorMap;
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

    public boolean isVerbose() {
        return verbose;
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
        builder.modelContexts.putAll(this.modelContextSelectorMap.getModelContextSelectors());
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
        private final Map<TargetSelector, ModelContext<?>> modelContexts = new LinkedHashMap<>();
        private final Set<TargetSelector> ignoredTargets = new LinkedHashSet<>();
        private final Set<TargetSelector> nullableTargets = new LinkedHashSet<>();
        private final SetterSelectorHolder setMethodSelectorHolder = new SetterSelectorHolder();
        private final SelectorProcessor selectorProcessor;
        private Settings settings;
        private Integer maxDepth;
        private Long seed;
        private Boolean lenient;
        private boolean verbose;

        private Builder(final Type rootType) {
            ApiValidator.validateRootClass(rootType);
            this.rootType = rootType;
            this.rootClass = TypeUtils.getRawType(this.rootType);
            this.selectorProcessor = new SelectorProcessor(
                    rootClass, INTERNAL_SERVICE_PROVIDERS, setMethodSelectorHolder);
        }

        private SetterSelectorHolder getSetMethodSelectorHolder() {
            return setMethodSelectorHolder;
        }

        public Builder<T> withRootTypeParameters(final List<Type> rootTypeParameters) {
            ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);
            this.rootTypeParameters.addAll(rootTypeParameters);
            return this;
        }

        private <V> Builder<T> putSelector(
                final Map<TargetSelector, V> map,
                final TargetSelector selector,
                final V value) {

            final List<TargetSelector> processed = selectorProcessor.process(selector);
            processed.forEach(s -> map.put(s, value));
            return this;
        }

        public Builder<T> withSubtype(final TargetSelector selector, final Class<?> subtype) {
            ApiValidator.notNull(subtype, "subtype must not be null");
            return putSelector(subtypeSelectors, selector, subtype);
        }

        public Builder<T> withGenerator(final TargetSelector selector, final Generator<?> generator) {
            ApiValidator.validateGeneratorNotNull(generator);
            return putSelector(generatorSelectors, selector, generator);
        }

        public Builder<T> withSupplier(final TargetSelector selector, final Supplier<?> supplier) {
            ApiValidator.validateSupplierNotNull(supplier);
            return withGenerator(selector, GeneratorDecorator.decorate(supplier));
        }

        public <V> Builder<T> withGeneratorSpec(final TargetSelector selector, final GeneratorSpecProvider<V> spec) {
            ApiValidator.validateGenerateSecondArgument(spec);
            return putSelector(generatorSpecSelectors, selector, spec);
        }

        public Builder<T> withOnCompleteCallback(final TargetSelector selector, final OnCompleteCallback<?> callback) {
            return putSelector(onCompleteCallbacks, selector, callback);
        }

        public Builder<T> withIgnored(final TargetSelector selector) {
            this.ignoredTargets.addAll(selectorProcessor.process(selector));
            return this;
        }

        public Builder<T> withNullable(final TargetSelector selector) {
            this.nullableTargets.addAll(selectorProcessor.process(selector));
            return this;
        }

        public Builder<T> withMaxDepth(final int maxDepth) {
            ApiValidator.isTrue(maxDepth >= 0, "Maximum depth must not be negative: %s", maxDepth);
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder<T> withAssignments(final Assignment... assignments) {
            ApiValidator.notNull(assignments, "assignments array must not be null");

            for (Assignment assignment : assignments) {
                ApiValidator.notNull(assignment, "assignments array must not contain null");
                processAssignment(assignment);
            }
            return this;
        }

        private void processAssignment(final Assignment assignment) {
            final List<InternalAssignment> assignments = ((Flattener<InternalAssignment>) assignment).flatten();

            for (InternalAssignment a : assignments) {
                final List<TargetSelector> origin = selectorProcessor.process(a.getOrigin());
                final List<TargetSelector> destinations = selectorProcessor.process(a.getDestination());

                Verify.isTrue(origin.size() == 1, "Origin has multiple selectors");

                for (TargetSelector destination : destinations) {
                    final Assignment processedAssignment = a.toBuilder()
                            .origin(origin.get(0))
                            .destination(destination)
                            .build();

                    this.assignmentSelectors
                            .computeIfAbsent(destination, k -> new ArrayList<>())
                            .add(processedAssignment);
                }
            }
        }

        public <V> Builder<T> withSetting(final SettingKey<V> key, final V value) {
            if (settings == null) {
                settings = Settings.create();
            } else if (settings.isLocked()) {
                settings = Settings.from(settings);
            }
            settings.set(key, value);
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

        public Builder<T> verbose() {
            this.verbose = true;
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
            modelContexts.putAll(otherContext.modelContextSelectorMap.getModelContextSelectors());

            // Increment max depth to account for the additional layer added by the collection
            maxDepth = otherContext.maxDepth == null ? null : otherContext.maxDepth + 1;
            settings = Settings.from(otherContext.settings)
                    .set(Keys.MAX_DEPTH, otherContext.settings.get(Keys.MAX_DEPTH) + 1)
                    .lock();

            return this;
        }

        /**
         * Copies (only) selectors from {@code otherContext} to this context.
         * Other data, such as maxDepth, seed, and settings are <b>not</b> copied.
         */
        public void withModel(final TargetSelector modelSelector, final Model<?> model) {
            final ModelContext<?> otherCtx = ((InternalModel<?>) model).getModelContext();
            final List<TargetSelector> processedSelectors = selectorProcessor.process(modelSelector);

            for (TargetSelector modelTarget : processedSelectors) {
                modelContexts.put(modelTarget, otherCtx);

            }
        }

        public ModelContext<T> build() {
            return new ModelContext<>(this);
        }
    }

    private void copyToThisContext(final TargetSelector modelTarget, final ModelContext<?> otherCtx) {

        otherCtx.generatorSelectorMap.getGeneratorSelectors().forEach((selector, generator) -> {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            generatorSelectorMap.putGenerator(resolvedSelector, generator);
        });

        otherCtx.generatorSelectorMap.getGeneratorSpecSelectors().forEach((selector, generator) -> {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            generatorSelectorMap.putGeneratorSpec(resolvedSelector, generator);
        });

        otherCtx.ignoredSelectorMap.getTargetSelectors().forEach(selector -> {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            ignoredSelectorMap.add(resolvedSelector);
        });

        otherCtx.nullableSelectorMap.getTargetSelectors().forEach(selector -> {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            nullableSelectorMap.add(resolvedSelector);
        });

        otherCtx.onCompleteCallbackSelectorMap.getOnCompleteCallbackSelectors().forEach((selector, callback) -> {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            onCompleteCallbackSelectorMap.put(resolvedSelector, callback);
        });

        otherCtx.assignmentSelectorMap.getAssignmentSelectors().forEach((selector, assignments) -> {
            final List<Assignment> updatedAssignments = new ArrayList<>(assignments.size());

            for (Assignment assignment : assignments) {
                final InternalAssignment a = (InternalAssignment) assignment;
                final TargetSelector updatedOrigin = applyModelSelectorScopes(modelTarget, a.getOrigin());
                final TargetSelector updatedDestination = applyModelSelectorScopes(modelTarget, a.getDestination());
                final InternalAssignment updatedAssignment = a.toBuilder()
                        .origin(updatedOrigin)
                        .destination(updatedDestination)
                        .build();

                updatedAssignments.add(updatedAssignment);
            }

            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            assignmentSelectorMap.put(resolvedSelector, updatedAssignments);
        });

        otherCtx.subtypeSelectorMap.getSubtypeSelectors().forEach((selector, subtype) -> {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            subtypeSelectorMap.put(resolvedSelector, subtype);
        });
    }
}