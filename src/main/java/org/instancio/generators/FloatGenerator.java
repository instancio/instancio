package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

public class FloatGenerator implements Generator<Float> {

    @Override
    public Float generate() {
        return Random.floatBetween(Float.MIN_VALUE, Float.MAX_VALUE);
    }
}
