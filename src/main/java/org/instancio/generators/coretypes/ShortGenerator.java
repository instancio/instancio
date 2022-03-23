package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;

public class ShortGenerator extends AbstractRandomGenerator<Short> implements NumberGeneratorSpec<Short> {

    private short min = Short.MIN_VALUE;
    private short max = Short.MAX_VALUE;

    public ShortGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Short> min(final Short min) {
        this.min = ObjectUtils.defaultIfNull(min, Short.MIN_VALUE);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Short> max(final Short max) {
        this.max = ObjectUtils.defaultIfNull(max, Short.MAX_VALUE);
        return this;
    }

    @Override
    public Short generate() {
        return random().shortBetween(min, max);
    }
}
