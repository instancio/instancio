package org.instancio.generators;

import org.instancio.internal.model.ModelContext;

import java.util.UUID;

public class UUIDGenerator extends AbstractRandomGenerator<UUID> {

    public UUIDGenerator(final ModelContext<?> context) {
        super(context);
    }

    @Override
    public UUID generate() {
        final byte[] randomBytes = new byte[16];
        for (int i = 0; i < randomBytes.length; i++) {
            randomBytes[i] = random().byteBetween(Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        return UUID.nameUUIDFromBytes(randomBytes);
    }

}
