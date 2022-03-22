package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

public class ByteGenerator implements Generator<Byte> {

    @Override
    public Byte generate() {
        return Random.byteBetween(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
}
