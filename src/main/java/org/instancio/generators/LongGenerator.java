package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class LongGenerator extends AbstractGenerator<Long> {

    public LongGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Long generate() {
        return random().longBetween(Long.MIN_VALUE, Long.MAX_VALUE);
    }
}
