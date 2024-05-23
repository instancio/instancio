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
import org.instancio.CartesianProductApi;
import org.instancio.FilterPredicate;
import org.instancio.GeneratorSpecProvider;
import org.instancio.Model;
import org.instancio.OnCompleteCallback;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.TypeTokenSupplier;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.reflect.ParameterizedTypeImpl;
import org.instancio.internal.util.CartesianList;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CartesianProductApiImpl<T> implements CartesianProductApi<T> {

    private final ModelContext.Builder<T> modelContextBuilder;
    private final List<CartesianValues> cartesianValues = new ArrayList<>();

    public CartesianProductApiImpl(final Type klass) {
        this.modelContextBuilder = ModelContext.builder(new ParameterizedTypeImpl(List.class, klass));
    }

    public CartesianProductApiImpl(final TypeTokenSupplier<T> typeToken) {
        this(ApiValidator.validateTypeToken(typeToken));
    }

    public <E> CartesianProductApiImpl(final Model<E> elementModel) {
        final InternalModel<E> model = (InternalModel<E>) elementModel;
        this.modelContextBuilder = ModelContext.<T>builder(List.class)
                .useModelAsTypeArgument(model.getModelContext());
    }

    @SafeVarargs
    @Override
    public final <V> CartesianProductApi<T> with(final TargetSelector selector, final V... values) {
        ApiValidator.notEmpty(values, "with() requires a non-empty array, but got: %s", Arrays.toString(values));
        final CartesianValues cv = new CartesianValues(selector, (Object[]) values);
        cartesianValues.add(cv);
        return this;
    }

    @Override
    public CartesianProductApi<T> ignore(final TargetSelector selector) {
        modelContextBuilder.withIgnored(selector);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> generate(
            final TargetSelector selector,
            final GeneratorSpecProvider<V> gen) {

        modelContextBuilder.withGeneratorSpec(selector, gen);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> generate(
            final TargetSelector selector,
            final GeneratorSpec<V> spec) {

        modelContextBuilder.withGenerator(selector, (Generator<T>) spec);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> onComplete(
            final TargetSelector selector,
            final OnCompleteCallback<V> callback) {

        modelContextBuilder.withOnCompleteCallback(selector, callback);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> filter(
            final TargetSelector selector,
            final FilterPredicate<V> predicate) {

        modelContextBuilder.filter(selector, predicate);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> set(final TargetSelector selector, final V value) {
        modelContextBuilder.withSupplier(selector, () -> value);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> setModel(final TargetSelector selector, final Model<V> model) {
        modelContextBuilder.withModel(selector, model);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> supply(
            final TargetSelector selector,
            final Generator<V> generator) {

        modelContextBuilder.withGenerator(selector, generator);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> supply(
            final TargetSelector selector,
            final Supplier<V> supplier) {

        modelContextBuilder.withSupplier(selector, supplier);
        return this;
    }

    @Override
    public CartesianProductApi<T> subtype(
            final TargetSelector selector,
            final Class<?> subtype) {

        modelContextBuilder.withSubtype(selector, subtype);
        return this;
    }

    @Override
    public CartesianProductApi<T> assign(final Assignment... assignments) {
        modelContextBuilder.withAssignments(assignments);
        return this;
    }

    @Override
    public CartesianProductApi<T> withBlank(final TargetSelector selector) {
        modelContextBuilder.withBlank(selector);
        return this;
    }

    @Override
    public CartesianProductApi<T> withSeed(final long seed) {
        modelContextBuilder.withSeed(seed);
        return this;
    }

    @Override
    public CartesianProductApi<T> withMaxDepth(final int maxDepth) {
        modelContextBuilder.withMaxDepth(maxDepth);
        return this;
    }

    @Override
    public CartesianProductApi<T> withNullable(final TargetSelector selector) {
        modelContextBuilder.withNullable(selector);
        return this;
    }

    @Override
    public <V> CartesianProductApi<T> withSetting(final SettingKey<V> key, final V value) {
        modelContextBuilder.withSetting(key, value);
        return this;
    }

    @Override
    public CartesianProductApi<T> withSettings(final Settings settings) {
        modelContextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public CartesianProductApi<T> lenient() {
        modelContextBuilder.lenient();
        return this;
    }

    @Override
    public CartesianProductApi<T> verbose() {
        modelContextBuilder.verbose();
        return this;
    }

    @Override
    public List<T> list() {
        final List<List<Object>> cartesianInputs = new ArrayList<>();
        for (CartesianValues rangeValue : cartesianValues) {
            cartesianInputs.add(rangeValue.values);
        }

        final List<List<Object>> combinations = CartesianList.create(cartesianInputs);
        final Map<TargetSelector, List<Object>> selectorRangeMap = getSelectorValues(combinations);

        for (Map.Entry<TargetSelector, List<Object>> entry : selectorRangeMap.entrySet()) {
            final TargetSelector selector = entry.getKey();
            final List<Object> valuesToEmit = entry.getValue();
            modelContextBuilder.withGeneratorSpec(selector, gen -> gen.emit()
                    // Some items might be unused due to generation parameters specified by the user
                    .ignoreUnused()
                    // Fail because this should not happen unless something went wrong
                    .whenEmptyThrowException()
                    .items(valuesToEmit));
        }

        modelContextBuilder.withGeneratorSpec(Select.root(), gen -> gen.collection().size(combinations.size()));

        final InternalModel<T> model = new InternalModel<>(modelContextBuilder.build());
        InternalModelDump.printVerbose(model);

        return new InstancioEngine(model).createRootObject();
    }

    private Map<TargetSelector, List<Object>> getSelectorValues(final List<List<Object>> combinations) {
        final Map<TargetSelector, List<Object>> selectorValues = new LinkedHashMap<>();

        for (List<Object> comboValues : combinations) {

            for (int i = 0; i < comboValues.size(); i++) {
                final Object value = comboValues.get(i);
                final TargetSelector selector = cartesianValues.get(i).selector;

                selectorValues
                        .computeIfAbsent(selector, k -> new ArrayList<>())
                        .add(value);
            }
        }
        return selectorValues;
    }

    private static final class CartesianValues {
        private final TargetSelector selector;
        private final List<Object> values;

        CartesianValues(TargetSelector selector, Object... values) {
            this.selector = selector;
            this.values = Arrays.asList(values);
        }
    }
}
