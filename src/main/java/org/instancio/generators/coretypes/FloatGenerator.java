package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;

public class FloatGenerator extends AbstractRandomGenerator<Float> {

    public FloatGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Float generate() {
        return random().floatBetween(Float.MIN_VALUE, Float.MAX_VALUE);
    }
}
