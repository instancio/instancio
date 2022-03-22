package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class ByteGenerator extends AbstractGenerator<Byte> {

    public ByteGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Byte generate() {
        return random().byteBetween(Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
}
