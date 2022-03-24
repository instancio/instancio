package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class DoubleGenerator extends AbstractRandomGenerator<Double> implements NumberGeneratorSpec<Double> {

    private double min = DEFAULT_DOUBLE_MIN;
    private double max = DEFAULT_DOUBLE_MAX;

    public DoubleGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Double> min(final Double min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Double> max(final Double max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public Double generate() {
        return random().doubleBetween(min, max);
    }
}
