package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class FloatGenerator extends AbstractGenerator<Float> {

    public FloatGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Float generate() {
        return random().floatBetween(Float.MIN_VALUE, Float.MAX_VALUE);
    }
}
