package org.instancio;

import org.instancio.generator.ValueGenerator;
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
 * @see org.instancio.generator.ValueGenerator
 */
public final class Generators {

    private Generators() {
        // non-instantiable
    }

    public static ValueGenerator<String> withPrefix(String prefix) {
        return () -> prefix + Random.positiveInt();
    }

    @SuppressWarnings("unchecked")
    public static <T> ValueGenerator<T> oneOf(T... values) {
        return () -> Random.from(values);
    }

    public static <T> ValueGenerator<T> nullValue() {
        return () -> null;
    }

}
