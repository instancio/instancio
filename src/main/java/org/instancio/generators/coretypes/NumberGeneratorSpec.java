package org.instancio.generators.coretypes;

import org.instancio.GeneratorSpec;

public interface NumberGeneratorSpec<T extends Number> extends GeneratorSpec<T> {

    byte DEFAULT_BYTE_MIN = 1;
    byte DEFAULT_BYTE_MAX = Byte.MAX_VALUE;
    short DEFAULT_SHORT_MIN = 1;
    short DEFAULT_SHORT_MAX = 10_000;
    int DEFAULT_INT_MIN = 1;
    int DEFAULT_INT_MAX = 10_000;
    long DEFAULT_LONG_MIN = 1;
    long DEFAULT_LONG_MAX = 10_000;
    float DEFAULT_FLOAT_MIN = 1;
    float DEFAULT_FLOAT_MAX = 10_000;
    double DEFAULT_DOUBLE_MIN = 1;
    double DEFAULT_DOUBLE_MAX = 10_000;

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
