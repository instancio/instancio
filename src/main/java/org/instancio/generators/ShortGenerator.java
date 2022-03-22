package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

public class ShortGenerator implements Generator<Short> {

    @Override
    public Short generate() {
        return Random.shortBetween(Short.MIN_VALUE, Short.MAX_VALUE);
    }
}
