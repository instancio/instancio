package org.instancio.generators.coretypes;

import org.instancio.settings.Setting;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class ByteGenerator extends AbstractRandomGenerator<Byte> implements NumberGeneratorSpec<Byte> {

    private byte min = Setting.BYTE_MIN.defaultValue();
    private byte max = Setting.BYTE_MAX.defaultValue();

    public ByteGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public NumberGeneratorSpec<Byte> min(final Byte min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<Byte> max(final Byte max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public Byte generate() {
        return random().byteBetween(min, max);
    }
}
