package org.instancio;

import org.instancio.internal.GeneratedHints;

/**
 * Generic interface for generating values.
 *
 * @param <T> type to generate
 * @see org.instancio.Generators
 */
@FunctionalInterface
public interface Generator<T> extends GeneratorSpec<T> {

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
     *         .supply(field("age"), oneOf(20, 30, 40, 50))
     *         .supply(field("location"), () -> "Canada")
     *         .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
     *         .create();
     * }</pre>
     *
     * @return generated value
     */
    T generate();

    default GeneratedHints getHints() {
        // ignore children by default to ensure values created
        // from user-supplied generators are not modified
        return GeneratedHints.builder().ignoreChildren(true).build();
    }
}
