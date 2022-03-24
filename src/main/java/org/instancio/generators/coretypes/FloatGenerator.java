package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class FloatGenerator extends AbstractRandomGenerator<Float> implements NumberGeneratorSpec<Float> {

    private float min = DEFAULT_FLOAT_MIN;
    private float max = DEFAULT_FLOAT_MAX;

    public FloatGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Float> min(final Float min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Float> max(final Float max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public Float generate() {
        return random().floatBetween(min, max);
    }
}
