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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.array.ArrayGenerator;
import org.instancio.generator.array.ArrayGeneratorSpec;
import org.instancio.generator.array.OneOfArrayGenerator;
import org.instancio.generator.array.OneOfArrayGeneratorSpec;
import org.instancio.generator.lang.ByteGenerator;
import org.instancio.generator.lang.DoubleGenerator;
import org.instancio.generator.lang.FloatGenerator;
import org.instancio.generator.lang.IntegerGenerator;
import org.instancio.generator.lang.LongGenerator;
import org.instancio.generator.lang.NumberGeneratorSpec;
import org.instancio.generator.lang.ShortGenerator;
import org.instancio.generator.lang.StringGenerator;
import org.instancio.generator.lang.StringGeneratorSpec;
import org.instancio.generator.math.BigDecimalGenerator;
import org.instancio.generator.math.BigIntegerGenerator;
import org.instancio.generator.util.CollectionGeneratorSpec;
import org.instancio.generator.util.CollectionGeneratorSpecImpl;
import org.instancio.generator.util.MapGeneratorSpec;
import org.instancio.generator.util.MapGeneratorSpecImpl;
import org.instancio.generator.util.OneOfCollectionGenerator;
import org.instancio.generator.util.OneOfCollectionGeneratorSpec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 * Provides built-in generators for overriding data generation parameters
 * such as number ranges, collection sizes, string lengths, etc.
 */
public class Generators {

    private final GeneratorContext context;

    public Generators(final GeneratorContext context) {
        this.context = context;
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

    public NumberGeneratorSpec<BigInteger> bigInteger() {
        return new BigIntegerGenerator(context);
    }

    public NumberGeneratorSpec<BigDecimal> bigDecimal() {
        return new BigDecimalGenerator(context);
    }

    @SafeVarargs
    public final <T> OneOfArrayGeneratorSpec<T> oneOf(T... values) {
        return new OneOfArrayGenerator<T>(context).oneOf(values);
    }

    public final <T> OneOfCollectionGeneratorSpec<T> oneOf(Collection<T> values) {
        return new OneOfCollectionGenerator<T>(context).oneOf(values);
    }

    /**
     * Creates a spec builder for arrays.
     *
     * @param <T> array component type
     * @return spec builder
     */
    public <T> ArrayGeneratorSpec<T> array() {
        return new ArrayGenerator<>(context);
    }

    /**
     * Creates a spec builder for {@link java.util.Collection} and its subtypes.
     *
     * @param <T> element type
     * @return spec builder
     */
    public <T> CollectionGeneratorSpec<Collection<T>> collection() {
        return new CollectionGeneratorSpecImpl<>(context);
    }

    /**
     * Creates a spec builder for {@link java.util.Map} and its subtypes.
     *
     * @param <K> key type
     * @param <V> value type
     * @return spec builder
     */
    public <K, V> MapGeneratorSpec<K, V> map() {
        return new MapGeneratorSpecImpl<>(context);
    }
}
