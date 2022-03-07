package org.instancio.generator;

/**
 * Generic interface for generating values.
 *
 * @param <T> type to generate
 * @see org.instancio.Generators
 */
public interface ValueGenerator<T> {

    /**
     * Depending on implementation, repeated invocations may return different values.
     * Some implementations may return random values on each invocation, while others
     * may return predefined values or random values from a given set.
     * <p>
     * Generators can be passed in as a lambda function. The following example
     * shows how to override generation strategy for certain fields.
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .with("name", withPrefix("name-"))  // returns a random string prefix with "name-"
     *         .with("age", oneOf(20, 30, 40, 50)) // returns a random value from the given array
     *         .with("lastModified", () -> LocalDateTime.now()) // returns current time each time
     *         .with("location", () -> "Canada") // returns the same value "Canada" each time
     *         .create();
     * }</pre>
     *
     * @return generated value
     */
    T generate();
}
