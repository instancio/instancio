package org.instancio.generator;

import org.instancio.util.Random;

public class ShortGenerator implements ValueGenerator<Short> {

    @Override
    public Short generate() {
        return Random.shortBetween(Short.MIN_VALUE, Short.MAX_VALUE);
    }
}
