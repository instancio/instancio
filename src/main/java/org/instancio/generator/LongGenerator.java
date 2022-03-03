package org.instancio.generator;

import org.instancio.util.Random;

public class LongGenerator implements ValueGenerator<Long> {

    @Override
    public Long generate() {
        return Random.longBetween(Long.MIN_VALUE, Long.MAX_VALUE);
    }
}
