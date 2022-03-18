package org.instancio.generator;

import org.instancio.util.Random;

public class IntegerGenerator implements Generator<Integer> {

    @Override
    public Integer generate() {
        return Random.intBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
