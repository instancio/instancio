package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;

public class FloatGenerator extends AbstractRandomGenerator<Float> implements NumberGeneratorSpec<Float> {

    private float min = Float.MIN_VALUE;
    private float max = Float.MAX_VALUE;

    public FloatGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Float> min(final Float min) {
        this.min = ObjectUtils.defaultIfNull(min, Float.MIN_VALUE);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Float> max(final Float max) {
        this.max = ObjectUtils.defaultIfNull(max, Float.MAX_VALUE);
        return this;
    }

    @Override
    public Float generate() {
        return random().floatBetween(min, max);
    }
}
