package org.instancio;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * A supplier that provides {@link Type} information.
 *
 * @param <T> type being supplied.<p>
 *            Required to be present, though not used directly.
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface TypeTokenSupplier<T> extends Supplier<Type> {

    /**
     * Returns type information.
     *
     * @return type
     */
    Type get();
}