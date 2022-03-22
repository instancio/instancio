package org.instancio;

/**
 * Instancio API for defining type parameters for generic classes.
 *
 * @param <T> type being created
 */
public interface InstancioOfClassApi<T> extends InstancioApi<T> {

    /**
     * Method for supplying type parameters for generic classes.
     * <p>
     * Example:
     * <pre>{@code
     *     List<Address> addresses = Instancio.of(List.class)
     *             .withTypeParameters(Address.class)
     *             .create();
     * }</pre>
     *
     * @param type one or more types
     * @return API builder reference
     */
    InstancioApi<T> withTypeParameters(Class<?>... type);
}
