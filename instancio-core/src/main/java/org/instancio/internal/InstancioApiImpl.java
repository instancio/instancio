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
package org.instancio.internal;

import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.OnCompleteCallback;
import org.instancio.Result;
import org.instancio.TargetSelector;
import org.instancio.TypeTokenSupplier;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.context.ModelContext;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class InstancioApiImpl<T> implements InstancioApi<T> {

    private final ModelContext.Builder<T> modelContextBuilder;

    public InstancioApiImpl(@NotNull final Class<T> klass) {
        this.modelContextBuilder = ModelContext.builder(ApiValidator.validateRootClass(klass));
    }

    public InstancioApiImpl(@NotNull final TypeTokenSupplier<T> typeToken) {
        this.modelContextBuilder = ModelContext.builder(ApiValidator.validateTypeToken(typeToken));
    }

    public InstancioApiImpl(@NotNull final Model<T> model) {
        final InternalModel<T> suppliedModel = (InternalModel<T>) model;
        final ModelContext<T> suppliedContext = suppliedModel.getModelContext();
        // copy context data to allow overriding
        this.modelContextBuilder = suppliedContext.toBuilder();
    }

    protected void addTypeParameters(final Class<?>... type) {
        modelContextBuilder.withRootTypeParameters(Arrays.asList(type));
    }

    @Override
    public InstancioApi<T> ignore(@NotNull final TargetSelector selectorGroup) {
        modelContextBuilder.withIgnored(selectorGroup);
        return this;
    }

    @Override
    public InstancioApi<T> withNullable(@NotNull final TargetSelector target) {
        modelContextBuilder.withNullable(target);
        return this;
    }

    @Override
    public <V> InstancioApi<T> supply(
            @NotNull final TargetSelector selectorGroup,
            @NotNull final Generator<V> generator) {

        ApiValidator.validateSupplierOrGenerator(generator);
        modelContextBuilder.withGenerator(selectorGroup, generator);
        return this;
    }

    @Override
    public <V> InstancioApi<T> set(
            @NotNull final TargetSelector selectorGroup,
            @Nullable final V value) {

        modelContextBuilder.withSupplier(selectorGroup, () -> value);
        return this;
    }

    @Override
    public <V> InstancioApi<T> supply(
            @NotNull final TargetSelector selectorGroup,
            @NotNull final Supplier<V> supplier) {

        ApiValidator.validateSupplierOrGenerator(supplier);
        modelContextBuilder.withSupplier(selectorGroup, supplier);
        return this;
    }

    @Override
    public <V, S extends GeneratorSpec<V>> InstancioApi<T> generate(
            @NotNull final TargetSelector target,
            @NotNull final Function<Generators, S> gen) {

        ApiValidator.validateGeneratorFunction(gen);
        modelContextBuilder.withGeneratorSpec(target, gen);
        return this;
    }

    @Override
    public <V> InstancioApi<T> onComplete(
            @NotNull final TargetSelector target,
            @NotNull final OnCompleteCallback<V> callback) {

        modelContextBuilder.withOnCompleteCallback(target, callback);
        return this;
    }

    @Override
    public InstancioApi<T> subtype(
            @NotNull final TargetSelector selectorGroup,
            @NotNull final Class<?> subtype) {

        modelContextBuilder.withSubtype(selectorGroup, subtype);
        return this;
    }

    @Override
    public InstancioApi<T> withSettings(@NotNull final Settings settings) {
        modelContextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public InstancioApi<T> withSeed(final long seed) {
        modelContextBuilder.withSeed(seed);
        return this;
    }

    @Override
    public InstancioApi<T> lenient() {
        modelContextBuilder.lenient();
        return this;
    }

    @NotNull
    @Override
    public Model<T> toModel() {
        return createModel();
    }

    @Override
    public T create() {
        final InstancioEngine engine = new InstancioEngine(createModel());
        return engine.createRootObject();
    }

    @NotNull
    @Override
    public Result<T> asResult() {
        final InternalModel<T> model = createModel();
        final InstancioEngine engine = new InstancioEngine(model);
        return new Result<>(engine.createRootObject(), model.getModelContext().getRandom().getSeed());
    }

    @NotNull
    @Override
    public Stream<T> stream() {
        final InternalModel<T> model = createModel();
        return Stream.generate(() -> new InstancioEngine(model).createRootObject());
    }

    private InternalModel<T> createModel() {
        return new InternalModel<>(modelContextBuilder.build());
    }
}