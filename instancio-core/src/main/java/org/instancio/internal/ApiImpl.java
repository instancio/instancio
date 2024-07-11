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
package org.instancio.internal;

import org.instancio.Assignment;
import org.instancio.FilterPredicate;
import org.instancio.GeneratorSpecProvider;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.OnCompleteCallback;
import org.instancio.Random;
import org.instancio.Result;
import org.instancio.TargetSelector;
import org.instancio.TypeTokenSupplier;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedProvider;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.context.ModelContext;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ApiImpl<T> implements InstancioApi<T> {

    private final ModelContext.Builder<T> modelContextBuilder;

    public ApiImpl(final Type klass) {
        this.modelContextBuilder = ModelContext.builder(klass);
    }

    public ApiImpl(final TypeTokenSupplier<T> typeToken) {
        this.modelContextBuilder = ModelContext.builder(ApiValidator.validateTypeToken(typeToken));
    }

    public ApiImpl(final Model<T> model) {
        final InternalModel<T> suppliedModel = (InternalModel<T>) model;
        final ModelContext<T> suppliedContext = suppliedModel.getModelContext();
        // copy context data to allow overriding
        this.modelContextBuilder = suppliedContext.toBuilder();
    }

    protected final void addTypeParameters(final Type... types) {
        modelContextBuilder.withRootTypeParameters(Arrays.asList(types));
    }

    @Override
    public InstancioApi<T> ignore(final TargetSelector selector) {
        modelContextBuilder.withIgnored(selector);
        return this;
    }

    @Override
    public <V> InstancioApi<T> generate(
            final TargetSelector selector,
            final GeneratorSpecProvider<V> gen) {

        modelContextBuilder.withGeneratorSpec(selector, gen);
        return this;
    }

    @Override
    public <V> InstancioApi<T> generate(
            final TargetSelector selector,
            final GeneratorSpec<V> spec) {

        modelContextBuilder.withGenerator(selector, (Generator<T>) spec);
        return this;
    }

    @Override
    public <V> InstancioApi<T> onComplete(
            final TargetSelector selector,
            final OnCompleteCallback<V> callback) {

        modelContextBuilder.withOnCompleteCallback(selector, callback);
        return this;
    }

    @Override
    public <V> InstancioApi<T> filter(final TargetSelector selector, final FilterPredicate<V> predicate) {
        modelContextBuilder.filter(selector, predicate);
        return this;
    }

    @Override
    public <V> InstancioApi<T> set(final TargetSelector selector, final V value) {
        modelContextBuilder.withSupplier(selector, () -> value);
        return this;
    }

    @Override
    public <V> InstancioApi<T> setModel(final TargetSelector selector, final Model<V> model) {
        modelContextBuilder.withModel(selector, model);
        return this;
    }

    @Override
    public <V> InstancioApi<T> supply(
            final TargetSelector selector,
            final Generator<V> generator) {

        modelContextBuilder.withGenerator(selector, generator);
        return this;
    }

    @Override
    public <V> InstancioApi<T> supply(
            final TargetSelector selector,
            final Supplier<V> supplier) {

        modelContextBuilder.withSupplier(selector, supplier);
        return this;
    }

    @Override
    public InstancioApi<T> subtype(
            final TargetSelector selector,
            final Class<?> subtype) {

        modelContextBuilder.withSubtype(selector, subtype);
        return this;
    }

    @Override
    public InstancioApi<T> assign(final Assignment... assignments) {
        modelContextBuilder.withAssignments(assignments);
        return this;
    }

    @Override
    public InstancioApi<T> setBlank(final TargetSelector selector) {
        modelContextBuilder.setBlank(selector);
        return this;
    }

    @Override
    public InstancioApi<T> withUnique(final TargetSelector selector) {
        modelContextBuilder.withUnique(selector);
        return this;
    }

    @Override
    public InstancioApi<T> applyFeed(final TargetSelector selector, final Feed feed) {
        modelContextBuilder.applyFeed(selector, feed);
        return this;
    }

    @Override
    public InstancioApi<T> applyFeed(final TargetSelector selector, final FeedProvider provider) {
        modelContextBuilder.applyFeed(selector, provider);
        return this;
    }

    @Override
    public InstancioApi<T> withSeed(final long seed) {
        modelContextBuilder.withSeed(seed);
        return this;
    }

    @Override
    public InstancioApi<T> withMaxDepth(final int maxDepth) {
        modelContextBuilder.withMaxDepth(maxDepth);
        return this;
    }

    @Override
    public InstancioApi<T> withNullable(final TargetSelector selector) {
        modelContextBuilder.withNullable(selector);
        return this;
    }

    @Override
    public <V> InstancioApi<T> withSetting(final SettingKey<V> key, final V value) {
        modelContextBuilder.withSetting(key, value);
        return this;
    }

    @Override
    public InstancioApi<T> withSettings(final Settings settings) {
        modelContextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public InstancioApi<T> lenient() {
        modelContextBuilder.lenient();
        return this;
    }

    @Override
    public InstancioApi<T> verbose() {
        modelContextBuilder.verbose();
        return this;
    }

    @Override
    public Model<T> toModel() {
        return createModel();
    }

    @Override
    public T create() {
        return createRootObject(createModel());
    }

    @Override
    public Result<T> asResult() {
        final InternalModel<T> model = createModel();
        final long seed = model.getModelContext().getRandom().getSeed();
        return new InternalResult<>(createRootObject(model), seed);
    }

    @Override
    public Stream<T> stream() {
        final AtomicBoolean modelDumped = new AtomicBoolean();
        final AtomicLong nextSeed = new AtomicLong();

        return Stream.generate(() -> {
            final InternalModel<T> model = new InternalModel<>(modelContextBuilder.build());

            // verbose() should print only once per stream()
            if (modelDumped.compareAndSet(false, true)) {
                InternalModelDump.printVerbose(model);
            }

            // Update seed for each stream element to avoid generating the same object
            final Random random = model.getModelContext().getRandom();
            nextSeed.set(random.longRange(1, Long.MAX_VALUE));

            modelContextBuilder.withSeed(nextSeed.get());
            return createRootObject(model);
        });
    }

    private T createRootObject(final InternalModel<T> model) {
        return new InstancioEngine(model).createRootObject();
    }

    private InternalModel<T> createModel() {
        final InternalModel<T> model = new InternalModel<>(modelContextBuilder.build());
        InternalModelDump.printVerbose(model);
        return model;
    }
}