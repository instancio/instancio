package org.instancio.generators;

import org.instancio.Generator;

import java.util.UUID;

public class UUIDGenerator implements Generator<UUID> {

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
