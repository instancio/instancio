package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.internal.random.RandomProvider;

public abstract class AbstractGenerator<T> implements Generator<T> {

    private final RandomProvider random;

    protected AbstractGenerator(final RandomProvider random) {
        this.random = random;
    }

    protected RandomProvider random() {
        return random;
    }
}
