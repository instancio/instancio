package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;

public class LongGenerator extends AbstractRandomGenerator<Long> {

    public LongGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Long generate() {
        return random().longBetween(Long.MIN_VALUE, Long.MAX_VALUE);
    }
}
