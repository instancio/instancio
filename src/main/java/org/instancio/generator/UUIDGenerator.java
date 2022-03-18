package org.instancio.generator;

import java.util.UUID;

public class UUIDGenerator implements Generator<UUID> {

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
