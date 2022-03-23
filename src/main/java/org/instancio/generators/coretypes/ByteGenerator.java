package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ObjectUtils;

public class ByteGenerator extends AbstractRandomGenerator<Byte> implements NumberGeneratorSpec<Byte> {

    private byte min = Byte.MIN_VALUE;
    private byte max = Byte.MAX_VALUE;

    public ByteGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Byte> min(final Byte min) {
        this.min = ObjectUtils.defaultIfNull(min, Byte.MIN_VALUE);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Byte> max(final Byte max) {
        this.max = ObjectUtils.defaultIfNull(max, Byte.MAX_VALUE);
        return this;
    }

    @Override
    public Byte generate() {
        return random().byteBetween(min, max);
    }
}
