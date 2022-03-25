package org.instancio.generators.coretypes;

import org.instancio.settings.Setting;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class LongGenerator extends AbstractRandomGenerator<Long> implements NumberGeneratorSpec<Long> {

    private long min = Setting.LONG_MIN.defaultValue();
    private long max = Setting.LONG_MAX.defaultValue();

    public LongGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Long> min(final Long min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Long> max(final Long max) {
        this.max = Verify.notNull(max);
        return this;
    }


    @Override
    public Long generate() {
        return random().longBetween(min, max);
    }
}
