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
import org.instancio.FilterPredicate;
import org.instancio.GeneratorSpecProvider;
import org.instancio.Model;
import org.instancio.OnCompleteCallback;
import org.instancio.Random;
import org.instancio.Scope;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedProvider;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;
import org.instancio.internal.InternalModel;
import org.instancio.internal.RandomHelper;
import org.instancio.internal.assignment.InternalAssignment;
import org.instancio.internal.feed.InternalFeedContext;
import org.instancio.internal.feed.InternalFeedProxy;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.BlankSelectors;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.SelectorProcessor;
import org.instancio.internal.selectors.SetterSelectorHolder;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProviderContext;
import org.instancio.internal.spi.InternalServiceProviderImpl;
import org.instancio.internal.spi.Providers;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
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
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;
import static org.instancio.internal.context.ModelContextHelper.buildRootTypeMap;
import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.ExcessivePublicCount"})
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
    private final ModelContextSource contextSource;
    private final SelectorMaps selectorMaps;
    private final boolean verbose;

    private ModelContext(final Builder<T> builder) {
        contextSource = builder.getModelContextSource();
        rootType = builder.rootType;
        rootTypeParameters = contextSource.getWithTypeParametersList();
        rootTypeMap = unmodifiableMap(buildRootTypeMap(builder.rootType, rootTypeParameters));
        seed = builder.seed;
        maxDepth = builder.maxDepth;
        verbose = builder.verbose;
        settings = createSettings(builder);
        random = RandomHelper.resolveRandom(settings.get(Keys.SEED), builder.seed);
        selectorMaps = new SelectorMaps(new GeneratorContext(settings, random));
        selectorMaps.initSelectorMaps(contextSource);
        providers = new Providers(new InternalServiceProviderContext(settings, random));
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
        final SelectorMap<Generator<?>> selectorMap = selectorMaps.getGeneratorSelectorMap().getSelectorMap();
        final UnusedEmitItemsReporter reporter = new UnusedEmitItemsReporter(selectorMap);
        reporter.report();
    }

    void reportUnusedSelectorWarnings() {
        if (settings.get(Keys.MODE) == Mode.STRICT && !selectorMaps.allEmpty()) {
            new UnusedSelectorReporter(getMaxDepth(), selectorMaps).report();
        }
    }

    public Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> getSelectors(final InternalNode rootNode) {
        return new SelectorNodeMatchesCollector(selectorMaps).getNodeMatches(rootNode);
    }

    public Type getRootType() {
        return rootType;
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

    public Integer getMaxDepth() {
        return defaultIfNull(maxDepth, settings.get(Keys.MAX_DEPTH));
    }

    public SelectorMaps getSelectorMaps() {
        return selectorMaps;
    }

    public boolean isIgnored(final InternalNode node) {
        return selectorMaps.getIgnoreSelectorMap().isTrue(node);
    }

    public boolean isNullable(final InternalNode node) {
        return selectorMaps.getWithNullableSelectorMap().isTrue(node);
    }

    public boolean isAccepted(final InternalNode node, final Object value) {
        Predicate<Object> predicate = selectorMaps.getFilterSelectorMap().getPredicate(node);
        if (predicate == null) {
            return true;
        }
        try {
            return predicate.test(value);
        } catch (Exception ex) {
            throw Fail.withUsageError(ErrorMessageUtils.filterPredicateErrorMessage(value, node, ex));
        }
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Optional<Generator<?>> getGenerator(final InternalNode node) {
        return selectorMaps.getGeneratorSelectorMap().getGenerator(node);
    }

    public void putGenerator(TargetSelector selector, Generator<?> generator) {
        selectorMaps.getGeneratorSelectorMap().putGenerator(selector, generator);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public List<OnCompleteCallback<?>> getCallbacks(final InternalNode node) {
        return selectorMaps.getOnCompleteSelectorMap().getCallbacks(node);
    }

    public BooleanSelectorMap getIgnoreSelectorMap() {
        return selectorMaps.getIgnoreSelectorMap();
    }

    public SubtypeSelectorMap getSubtypeSelectorMap() {
        return selectorMaps.getSubtypeSelectorMap();
    }

    public ModelContextSelectorMap getSetModelSelectorMap() {
        return selectorMaps.getSetModelSelectorMap();
    }

    public SelectorMap<Feed> getFeedSelectorMap() {
        return selectorMaps.getFeedSelectorMap().getSelectorMap();
    }

    public List<InternalAssignment> getAssignments(final InternalNode node) {
        return selectorMaps.getAssignmentSelectorMap().getAssignments(node);
    }

    public BooleanSelectorMap getAssignmentOriginSelectorMap() {
        return selectorMaps.getAssignmentSelectorMap().getOriginSelectors();
    }

    public List<TargetSelector> getAssignmentDestinationSelectors(final InternalNode node) {
        return selectorMaps.getAssignmentSelectorMap().getDestinationSelectors(node);
    }

    ModelContextSource getContextSource() {
        return contextSource;
    }

    public Builder<T> toBuilder() {
        final Builder<T> builder = new Builder<>(rootType);
        builder.maxDepth = this.maxDepth;
        builder.seed = this.seed;
        builder.settings = this.settings;
        builder.withTypeParametersList = new ArrayList<>(this.rootTypeParameters);
        builder.withNullableSet = new LinkedHashSet<>(this.contextSource.getWithNullableSet());
        builder.ignoreSet = new LinkedHashSet<>(this.contextSource.getIgnoreSet());
        builder.generatorMap = new LinkedHashMap<>(this.contextSource.getGeneratorMap());
        builder.generatorSpecMap = new LinkedHashMap<>(this.contextSource.getGeneratorSpecMap());
        builder.subtypeMap = new LinkedHashMap<>(this.contextSource.getSubtypeMap());
        builder.onCompleteMap = new LinkedHashMap<>(this.contextSource.getOnCompleteMap());
        builder.filterMap = new LinkedHashMap<>(this.contextSource.getFilterMap());
        builder.assignmentMap = new LinkedHashMap<>(this.contextSource.getAssignmentMap());
        builder.setModelMap = new LinkedHashMap<>(this.contextSource.getSetModelMap());
        builder.feedMap = new LinkedHashMap<>(this.contextSource.getFeedMap());
        return builder;
    }

    public static <T> Builder<T> builder(final Type rootType) {
        return new Builder<>(rootType);
    }

    @SuppressWarnings("PMD.TooManyFields")
    public static final class Builder<T> {
        private final Type rootType;
        private List<Type> withTypeParametersList;
        private Map<TargetSelector, Class<?>> subtypeMap;
        private Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecMap;
        private Map<TargetSelector, Generator<?>> generatorMap;
        private Map<TargetSelector, OnCompleteCallback<?>> onCompleteMap;
        private Map<TargetSelector, Predicate<?>> filterMap;
        private Map<TargetSelector, List<Assignment>> assignmentMap;
        private Map<TargetSelector, ModelContext<?>> setModelMap;
        private Map<TargetSelector, Function<GeneratorContext, Feed>> feedMap;
        private Set<TargetSelector> ignoreSet;
        private Set<TargetSelector> withNullableSet;
        private Settings settings;
        private Integer maxDepth;
        private Long seed;
        private Boolean lenient;
        private boolean verbose;
        private final SelectorProcessor selectorProcessor;
        private final SetterSelectorHolder setMethodSelectorHolder = new SetterSelectorHolder();

        private Builder(final Type rootType) {
            ApiValidator.validateRootClass(rootType);
            this.rootType = rootType;
            this.selectorProcessor = new SelectorProcessor(
                    TypeUtils.getRawType(rootType), INTERNAL_SERVICE_PROVIDERS, setMethodSelectorHolder);
        }

        private SetterSelectorHolder getSetMethodSelectorHolder() {
            return setMethodSelectorHolder;
        }

        public Builder<T> withRootTypeParameters(final List<Type> rootTypeParameters) {
            ApiValidator.validateTypeParameters(rootType, rootTypeParameters);
            this.withTypeParametersList = new ArrayList<>(rootTypeParameters);
            return this;
        }

        private <V> Builder<T> addSelector(
                final Map<TargetSelector, V> map,
                final TargetSelector selector,
                final V value) {

            final List<TargetSelector> processed = selectorProcessor.process(selector);
            for (TargetSelector s : processed) {
                map.put(s, value);
            }
            return this;
        }

        public Builder<T> withSubtype(final TargetSelector selector, final Class<?> subtype) {
            ApiValidator.notNull(subtype, "subtype must not be null");
            subtypeMap = CollectionUtils.newLinkedHashMapIfNull(subtypeMap);
            return addSelector(subtypeMap, selector, subtype);
        }

        public Builder<T> withGenerator(final TargetSelector selector, final Generator<?> generator) {
            ApiValidator.validateGeneratorNotNull(generator);
            generatorMap = CollectionUtils.newLinkedHashMapIfNull(generatorMap);
            return addSelector(generatorMap, selector, generator);
        }

        public Builder<T> withSupplier(final TargetSelector selector, final Supplier<?> supplier) {
            ApiValidator.validateSupplierNotNull(supplier);
            return withGenerator(selector, GeneratorDecorator.decorate(supplier));
        }

        public <V> Builder<T> withGeneratorSpec(final TargetSelector selector, final GeneratorSpecProvider<V> spec) {
            ApiValidator.validateGenerateSecondArgument(spec);
            generatorSpecMap = CollectionUtils.newLinkedHashMapIfNull(generatorSpecMap);
            return addSelector(generatorSpecMap, selector, spec);
        }

        public Builder<T> withOnCompleteCallback(final TargetSelector selector, final OnCompleteCallback<?> callback) {
            onCompleteMap = CollectionUtils.newLinkedHashMapIfNull(onCompleteMap);
            return addSelector(onCompleteMap, selector, callback);
        }

        public Builder<T> filter(final TargetSelector selector, final Predicate<?> predicate) {
            ApiValidator.notNull(predicate, "predicate must not be null");
            filterMap = CollectionUtils.newLinkedHashMapIfNull(filterMap);
            return addSelector(filterMap, selector, predicate);
        }

        public Builder<T> withUnique(final TargetSelector selector) {
            return filter(selector, new FilterPredicate<Object>() {
                final Set<Object> generatedValues = new HashSet<>();

                @Override
                public boolean test(final Object obj) {
                    return generatedValues.add(obj);
                }
            });
        }

        public Builder<T> applyFeed(final TargetSelector selector, final Feed feed) {
            return addFeedFunction(selector, generatorCtx -> feed);
        }

        public Builder<T> applyFeed(final TargetSelector selector, final FeedProvider provider) {
            return addFeedFunction(selector, generatorCtx -> {
                final FeedProvider.FeedBuilderFactory factory = new FeedProvider.FeedBuilderFactory() {};
                final InternalFeedContext.Builder<?> builder =
                        (InternalFeedContext.Builder<?>) provider.get(factory);

                return InternalFeedProxy.forClass(builder
                        .withGeneratorContext(generatorCtx)
                        .build());
            });
        }

        private Builder<T> addFeedFunction(
                final TargetSelector selector,
                final Function<GeneratorContext, Feed> feedFn) {

            feedMap = CollectionUtils.newLinkedHashMapIfNull(feedMap);
            return addSelector(feedMap, selector, feedFn);
        }

        public Builder<T> withIgnored(final TargetSelector selector) {
            ignoreSet = CollectionUtils.newLinkedHashSetIfNull(ignoreSet);
            ignoreSet.addAll(selectorProcessor.process(selector));
            return this;
        }

        public Builder<T> withNullable(final TargetSelector selector) {
            withNullableSet = CollectionUtils.newLinkedHashSetIfNull(withNullableSet);
            withNullableSet.addAll(selectorProcessor.process(selector));
            return this;
        }

        public Builder<T> withMaxDepth(final int maxDepth) {
            ApiValidator.isTrue(maxDepth >= 0, "Maximum depth must not be negative: %s", maxDepth);
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder<T> withAssignments(final Assignment... assignments) {
            ApiValidator.notNull(assignments, "assignments array must not be null");
            assignmentMap = CollectionUtils.newLinkedHashMapIfNull(assignmentMap);

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

                    this.assignmentMap
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

        public Builder<T> setBlank(final TargetSelector selector) {
            if (Objects.equals(selector, Select.root())) {
                setBlankTargets(); // special case for root selector (no scopes)
            } else {
                final List<TargetSelector> processedSelectors = selectorProcessor.process(selector);

                for (TargetSelector processedSelector : processedSelectors) {
                    final InternalSelector target = (InternalSelector) processedSelector;
                    final Scope[] effectiveScopes = CollectionUtils.combine(target.getScopes(), target.toScope())
                            .toArray(new Scope[0]);

                    setBlankTargets(effectiveScopes);
                }
            }
            return this;
        }

        private void setBlankTargets(final Scope... scopes) {
            withSupplier(BlankSelectors.leafSelector().within(scopes), () -> null);
            withGeneratorSpec(BlankSelectors.collectionSelector().within(scopes).lenient(), gen -> gen.collection().size(0));
            withGeneratorSpec(BlankSelectors.mapSelector().within(scopes).lenient(), gen -> gen.map().size(0));
            withGeneratorSpec(BlankSelectors.arraySelector().within(scopes).lenient(), gen -> gen.array().length(0));
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
            seed = otherContext.seed;
            withTypeParametersList = Collections.singletonList(otherContext.getRootType());

            final ModelContextSource src = otherContext.contextSource;
            withNullableSet = new LinkedHashSet<>(src.getWithNullableSet());
            ignoreSet = new LinkedHashSet<>(src.getIgnoreSet());
            generatorMap = new LinkedHashMap<>(src.getGeneratorMap());
            generatorSpecMap = new LinkedHashMap<>(src.getGeneratorSpecMap());
            subtypeMap = new LinkedHashMap<>(src.getSubtypeMap());
            onCompleteMap = new LinkedHashMap<>(src.getOnCompleteMap());
            filterMap = new LinkedHashMap<>(src.getFilterMap());
            assignmentMap = new LinkedHashMap<>(src.getAssignmentMap());
            setModelMap = new LinkedHashMap<>(src.getSetModelMap());
            feedMap = new LinkedHashMap<>(src.getFeedMap());

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
        public Builder<T> setModel(final TargetSelector modelSelector, final Model<?> model) {
            setModelMap = CollectionUtils.newLinkedHashMapIfNull(setModelMap);
            final ModelContext<?> otherCtx = ((InternalModel<?>) model).getModelContext();
            final List<TargetSelector> processedSelectors = selectorProcessor.process(modelSelector);

            for (TargetSelector modelTarget : processedSelectors) {
                setModelMap.put(modelTarget, otherCtx);
            }
            return this;
        }

        private ModelContextSource getModelContextSource() {
            return new ModelContextSource(
                    withTypeParametersList,
                    subtypeMap,
                    generatorSpecMap,
                    generatorMap,
                    onCompleteMap,
                    filterMap,
                    assignmentMap,
                    setModelMap,
                    feedMap,
                    ignoreSet,
                    withNullableSet);
        }

        public ModelContext<T> build() {
            return new ModelContext<>(this);
        }
    }
}