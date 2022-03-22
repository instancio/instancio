package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;

public class DoubleGenerator extends AbstractRandomGenerator<Double> {

    public DoubleGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Double generate() {
        return random().doubleBetween(Double.MIN_VALUE, Double.MAX_VALUE);
    }
}
