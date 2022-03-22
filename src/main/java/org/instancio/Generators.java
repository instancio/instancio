package org.instancio;

import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.CollectionGenerator;
import org.instancio.generators.IntegerGenerator;
import org.instancio.generators.MapGenerator;
import org.instancio.generators.StringGenerator;
import org.instancio.internal.random.RandomProvider;

/**
 * Defines a number of generators for various use-cases. These can be used to override default
 * value generators. If custom behaviour is required, a generator can be supplied using a lambda function.
 *
 * @see Generator
 */
public final class Generators {

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

    public StringGenerator string() {
        return new StringGenerator(random);
    }

    public IntegerGenerator ints() {
        return new IntegerGenerator(random);
    }

    @SafeVarargs
    public final <T> Generator<T> oneOf(T... values) {
        return () -> random.from(values);
    }

}
