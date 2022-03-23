package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;

public class LongGenerator extends AbstractRandomGenerator<Long> implements NumberGeneratorSpec<Long> {

    private long min = Long.MIN_VALUE;
    private long max = Long.MAX_VALUE;

    public LongGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Long> min(final Long min) {
        this.min = ObjectUtils.defaultIfNull(min, Long.MIN_VALUE);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Long> max(final Long max) {
        this.max = ObjectUtils.defaultIfNull(max, Long.MAX_VALUE);
        return this;
    }


    @Override
    public Long generate() {
        return random().longBetween(min, max);
    }
}
