package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class IntegerGenerator extends AbstractRandomGenerator<Integer> implements NumberGeneratorSpec<Integer> {

    private int min = DEFAULT_INT_MIN;
    private int max = DEFAULT_INT_MAX;

    public IntegerGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Integer> min(final Integer min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Integer> max(final Integer max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public Integer generate() {
        return random().intBetween(min, max);
    }
}
