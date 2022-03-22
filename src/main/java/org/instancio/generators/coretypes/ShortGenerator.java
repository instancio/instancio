package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;

public class ShortGenerator extends AbstractRandomGenerator<Short> {

    public ShortGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Short generate() {
        return random().shortBetween(Short.MIN_VALUE, Short.MAX_VALUE);
    }
}
