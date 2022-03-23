package org.instancio.generators.coretypes;

import org.instancio.GeneratorSpec;

public interface NumberGeneratorSpec<T extends Number> extends GeneratorSpec<T> {

    /**
     * Lower bound for the random number generator.
     *
     * @param min lower bound (inclusive)
     * @return spec builder
     */
    NumberGeneratorSpec<T> min(T min);

    /**
     * Upper bound for the random number generator.
     *
     * @param max upper bound (exclusive)
     * @return spec builder
     */
    NumberGeneratorSpec<T> max(T max);
}
