package org.instancio.generator;

import org.instancio.util.Random;

public class IntegerGenerator implements Generator<Integer> {

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerGenerator min(int min) {
        this.min = min;
        return this;
    }

    public IntegerGenerator max(int max) {
        this.max = max;
        return this;
    }

    @Override
    public Integer generate() {
        return Random.intBetween(min, max);
    }
}
