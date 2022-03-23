package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;

public class DoubleGenerator extends AbstractRandomGenerator<Double> implements NumberGeneratorSpec<Double> {

    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;

    public DoubleGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Double> min(final Double min) {
        this.min = ObjectUtils.defaultIfNull(min, Double.MIN_VALUE);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Double> max(final Double max) {
        this.max = ObjectUtils.defaultIfNull(max, Double.MAX_VALUE);
        return this;
    }

    @Override
    public Double generate() {
        return random().doubleBetween(min, max);
    }
}
