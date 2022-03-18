package org.instancio.generator;

import org.instancio.util.Random;

public class BooleanGenerator implements Generator<Boolean> {

    @Override
    public Boolean generate() {
        return Random.trueOrFalse();
    }
}
