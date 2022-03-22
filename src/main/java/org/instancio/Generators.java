package org.instancio;

import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.OneOfGenerator;
import org.instancio.generators.OneOfGeneratorSpec;
import org.instancio.generators.collections.CollectionGenerator;
import org.instancio.generators.collections.MapGenerator;
import org.instancio.generators.coretypes.IntegerGenerator;
import org.instancio.generators.coretypes.StringGenerator;
import org.instancio.generators.coretypes.StringGeneratorSpec;
import org.instancio.internal.random.RandomProvider;

/**
 * Provides built-int generators for fine-tuning data generation.
 */
public class Generators {

    private final RandomProvider random;

    public Generators(final RandomProvider random) {
        this.random = random;
    }

    public ArrayGenerator array() {
        return new ArrayGenerator();
    }

    public CollectionGenerator collection() {
        return new CollectionGenerator();
    }

    public MapGenerator map() {
        return new MapGenerator();
    }

    public StringGeneratorSpec string() {
        return new StringGenerator(random);
    }

    public IntegerGenerator ints() {
        return new IntegerGenerator(random);
    }

    @SafeVarargs
    public final <T> OneOfGeneratorSpec<T> oneOf(T... values) {
        return new OneOfGenerator<T>(random).oneOf(values);
    }
}
