package org.instancio.generators.collections;

import org.instancio.Generator;
import org.instancio.internal.GeneratorSettings;
import org.instancio.exception.InstancioException;
import org.instancio.util.Verify;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator implements Generator<Map<?, ?>> {

    private int size = 2;
    private Class<?> type = HashMap.class;

    public MapGenerator size(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.size = size;
        return this;
    }

    public MapGenerator type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public Map<?, ?> generate() {
        try {
            return (Map<?, ?>) type.newInstance();
        } catch (Exception ex) {
            throw new InstancioException(String.format("Error creating instance of: %s", type), ex);
        }
    }

    @Override
    public GeneratorSettings getSettings() {
        return GeneratorSettings.builder()
                .dataStructureSize(size)
                .ignoreChildren(false)
                .build();
    }
}
