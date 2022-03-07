package org.instancio.generator;

import org.instancio.util.Random;

public class IntegerGenerator implements ValueGenerator<Integer> {

    @Override
    public Integer generate() {
        return Random.intBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
