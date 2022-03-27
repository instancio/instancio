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
package org.instancio;

import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.ArrayGeneratorSpec;
import org.instancio.generators.OneOfArrayGenerator;
import org.instancio.generators.OneOfArrayGeneratorSpec;
import org.instancio.generators.OneOfCollectionGenerator;
import org.instancio.generators.OneOfCollectionGeneratorSpec;
import org.instancio.generators.collections.CollectionGeneratorSpec;
import org.instancio.generators.collections.CollectionGeneratorSpecImpl;
import org.instancio.generators.collections.MapGeneratorSpec;
import org.instancio.generators.collections.MapGeneratorSpecImpl;
import org.instancio.generators.coretypes.ByteGenerator;
import org.instancio.generators.coretypes.DoubleGenerator;
import org.instancio.generators.coretypes.FloatGenerator;
import org.instancio.generators.coretypes.IntegerGenerator;
import org.instancio.generators.coretypes.LongGenerator;
import org.instancio.generators.coretypes.NumberGeneratorSpec;
import org.instancio.generators.coretypes.ShortGenerator;
import org.instancio.generators.coretypes.StringGenerator;
import org.instancio.generators.coretypes.StringGeneratorSpec;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.random.RandomProvider;

import java.util.Collection;

/**
 * Provides built-in generators for overriding data generation parameters
 * such as number ranges, collection sizes, string lengths, etc.
 */
public class Generators {

    private final ModelContext<?> context;

    public Generators(final ModelContext<?> context) {
        this.context = context;
    }

    public RandomProvider random() {
        return context.getRandomProvider();
    }

    public StringGeneratorSpec string() {
        return new StringGenerator(context);
    }

    public NumberGeneratorSpec<Byte> bytes() {
        return new ByteGenerator(context);
    }

    public NumberGeneratorSpec<Short> shorts() {
        return new ShortGenerator(context);
    }

    public NumberGeneratorSpec<Integer> ints() {
        return new IntegerGenerator(context);
    }

    public NumberGeneratorSpec<Long> longs() {
        return new LongGenerator(context);
    }

    public NumberGeneratorSpec<Float> floats() {
        return new FloatGenerator(context);
    }

    public NumberGeneratorSpec<Double> doubles() {
        return new DoubleGenerator(context);
    }

    @SafeVarargs
    public final <T> OneOfArrayGeneratorSpec<T> oneOf(T... values) {
        return new OneOfArrayGenerator<T>(context).oneOf(values);
    }

    public final <T> OneOfCollectionGeneratorSpec<T> oneOf(Collection<T> values) {
        return new OneOfCollectionGenerator<T>(context).oneOf(values);
    }

    public <T> ArrayGeneratorSpec<T> array() {
        return new ArrayGenerator<>(context);
    }

    public <T> CollectionGeneratorSpec<Collection<T>> collection() {
        return new CollectionGeneratorSpecImpl<>(context);
    }

    public <K, V> MapGeneratorSpec<K, V> map() {
        return new MapGeneratorSpecImpl<>(context);
    }
}
