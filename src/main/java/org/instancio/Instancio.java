package org.instancio;

/**
 * TODO javadoc
 */
public class Instancio {

    private Instancio() {
        // non-instantiable
    }

    public static <T> ClassCreationApi<T> of(Class<T> klass) {
        return new ClassCreationApi<>(klass);
    }

    public static <T> GenericTypeCreationApi<T> of(TypeTokenSupplier<T> typeToken) {
        return new GenericTypeCreationApi<>(typeToken);
    }

    public static <T> CreationApi<T> of(Model<T> model) {
        return new ModelCreationApi<>(model);
    }
}
