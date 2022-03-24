package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;
import org.instancio.util.Verify;

public class ShortGenerator extends AbstractRandomGenerator<Short> implements NumberGeneratorSpec<Short> {

    private short min = DEFAULT_SHORT_MIN;
    private short max = DEFAULT_SHORT_MAX;

    public ShortGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Short> min(final Short min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Short> max(final Short max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public Short generate() {
        return random().shortBetween(min, max);
    }
}
