package org.instancio;

import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.CollectionGenerator;
import org.instancio.generators.IntegerGenerator;
import org.instancio.generators.MapGenerator;
import org.instancio.generators.StringGenerator;
import org.instancio.util.Random;

/**
 * Defines a number of generators for various use-cases. These can be used to override default
 * value generators. If custom behaviour is required, a generator can be supplied using a lambda function.
 *
 * <pre>{@code
 *     // Sets product code to a random value in the following format: "ABC-123"
 *     Product product = Instancio.of(Product.class)
 *         .with("productCode", () -> String.format("%s-%s", Random.alphabetic(3), Random.intBetween(100, 999)))
 *         .create();
 * }</pre>
 *
 * @see Generator
 */
public final class Generators {

    private Generators() {
        // non-instantiable
    }

    public static ArrayGenerator array() {
        return new ArrayGenerator();
    }

    public static CollectionGenerator collection() {
        return new CollectionGenerator();
    }

    public static MapGenerator map() {
        return new MapGenerator();
    }

    public static StringGenerator string() {
        return new StringGenerator();
    }

    public static IntegerGenerator ints() {
        return new IntegerGenerator();
    }

    @SuppressWarnings("unchecked")
    public static <T> Generator<T> oneOf(T... values) {
        return () -> Random.from(values);
    }

    public static <T> Generator<T> nullValue() {
        return () -> null;
    }

}
