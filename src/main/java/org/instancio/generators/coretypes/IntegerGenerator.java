package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;

public class IntegerGenerator extends AbstractRandomGenerator<Integer> implements NumberGeneratorSpec<Integer> {

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Integer> min(final Integer min) {
        this.min = ObjectUtils.defaultIfNull(min, Integer.MIN_VALUE);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Integer> max(final Integer max) {
        this.max = ObjectUtils.defaultIfNull(max, Integer.MAX_VALUE);
        return this;
    }

    @Override
    public Integer generate() {
        return random().intBetween(min, max);
    }
}
