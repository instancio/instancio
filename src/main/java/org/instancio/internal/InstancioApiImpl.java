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

import org.instancio.Binding;
import org.instancio.Generator;
import org.instancio.GeneratorSpec;
import org.instancio.Generators;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.TypeTokenSupplier;
import org.instancio.internal.model.InternalModel;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Settings;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

public class InstancioApiImpl<T> implements InstancioApi<T> {

    private final ModelContext.Builder<T> modelContextBuilder;

    public InstancioApiImpl(final Class<T> klass) {
        this.modelContextBuilder = ModelContext.builder(klass);
    }

    public InstancioApiImpl(final TypeTokenSupplier<T> typeToken) {
        final Type rootType = typeToken.get();
        this.modelContextBuilder = ModelContext.builder(rootType);
    }

    public InstancioApiImpl(final Model<T> model) {
        final InternalModel<T> suppliedModel = (InternalModel<T>) model;
        final ModelContext<T> suppliedContext = suppliedModel.getModelContext();
        // copy context data to allow overriding
        this.modelContextBuilder = suppliedContext.toBuilder();
    }

    protected void addTypeParameters(Class<?>... type) {
        modelContextBuilder.withRootTypeParameters(Arrays.asList(type));
    }

    @Override
    public InstancioApi<T> ignore(Binding binding) {
        modelContextBuilder.withIgnored(binding);
        return this;
    }

    @Override
    public InstancioApi<T> withNullable(Binding target) {
        modelContextBuilder.withNullable(target);
        return this;
    }

    @Override
    public <V> InstancioApi<T> supply(Binding binding, Generator<V> generator) {
        modelContextBuilder.withGenerator(binding, generator);
        return this;
    }

    @Override
    public <V, S extends GeneratorSpec<V>> InstancioApi<T> generate(final Binding target, final Function<Generators, S> gen) {
        modelContextBuilder.withGeneratorSpec(target, gen);
        return this;
    }

    @Override
    public InstancioApi<T> map(Class<?> baseClass, Class<?> subClass) {
        modelContextBuilder.withSubtypeMapping(baseClass, subClass);
        return this;
    }

    @Override
    public InstancioApi<T> withSettings(final Settings settings) {
        modelContextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public InstancioApi<T> withSeed(final int seed) {
        modelContextBuilder.withSeed(seed);
        return this;
    }

    @Override
    public Model<T> toModel() {
        return createModel();
    }

    @Override
    public T create() {
        final InstancioDriver instancioDriver = new InstancioDriver(createModel());
        return instancioDriver.createEntryPoint();
    }

    private InternalModel<T> createModel() {
        return new InternalModel<>(modelContextBuilder.build());
    }
}
