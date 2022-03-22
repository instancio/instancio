package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class ShortGenerator extends AbstractGenerator<Short> {

    public ShortGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Short generate() {
        return random().shortBetween(Short.MIN_VALUE, Short.MAX_VALUE);
    }
}
