package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class DoubleGenerator extends AbstractGenerator<Double> {

    public DoubleGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Double generate() {
        return random().doubleBetween(Double.MIN_VALUE, Double.MAX_VALUE);
    }
}
