package org.instancio;

import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.OneOfArrayGenerator;
import org.instancio.generators.OneOfArrayGeneratorSpec;
import org.instancio.generators.OneOfCollectionGenerator;
import org.instancio.generators.OneOfCollectionGeneratorSpec;
import org.instancio.generators.collections.CollectionGenerator;
import org.instancio.generators.collections.CollectionGeneratorSpec;
import org.instancio.generators.collections.MapGenerator;
import org.instancio.generators.collections.MapGeneratorSpec;
import org.instancio.generators.coretypes.ByteGenerator;
import org.instancio.generators.coretypes.DoubleGenerator;
import org.instancio.generators.coretypes.FloatGenerator;
import org.instancio.generators.coretypes.IntegerGenerator;
import org.instancio.generators.coretypes.LongGenerator;
import org.instancio.generators.coretypes.NumberGeneratorSpec;
import org.instancio.generators.coretypes.ShortGenerator;
import org.instancio.generators.coretypes.StringGenerator;
import org.instancio.generators.coretypes.StringGeneratorSpec;
import org.instancio.internal.random.RandomProvider;

import java.util.Collection;

/**
 * Provides built-int generators for fine-tuning data generation.
 */
public class Generators {

    private final RandomProvider random;

    public Generators(final RandomProvider random) {
        this.random = random;
    }

    public StringGeneratorSpec string() {
        return new StringGenerator(random);
    }

    public NumberGeneratorSpec<Byte> bytes() {
        return new ByteGenerator(random);
    }

    public NumberGeneratorSpec<Short> shorts() {
        return new ShortGenerator(random);
    }

    public NumberGeneratorSpec<Integer> ints() {
        return new IntegerGenerator(random);
    }

    public NumberGeneratorSpec<Long> longs() {
        return new LongGenerator(random);
    }

    public NumberGeneratorSpec<Float> floats() {
        return new FloatGenerator(random);
    }

    public NumberGeneratorSpec<Double> doubles() {
        return new DoubleGenerator(random);
    }

    @SafeVarargs
    public final <T> OneOfArrayGeneratorSpec<T> oneOf(T... values) {
        return new OneOfArrayGenerator<T>(random).oneOf(values);
    }

    public final <T> OneOfCollectionGeneratorSpec<T> oneOf(Collection<T> values) {
        return new OneOfCollectionGenerator<T>(random).oneOf(values);
    }

    public ArrayGenerator array() {
        return new ArrayGenerator();
    }

    public <T> CollectionGeneratorSpec<Collection<T>> collection() {
        return new CollectionGenerator<>(random);
    }

    public <K, V> MapGeneratorSpec<K, V> map() {
        return new MapGenerator<>(random);
    }
}
