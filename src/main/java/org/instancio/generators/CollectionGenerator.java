package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.internal.GeneratorSettings;
import org.instancio.exception.InstancioException;
import org.instancio.util.Verify;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionGenerator implements Generator<Collection<?>> {

    private int size = 2;
    private Class<?> type = ArrayList.class;

    public CollectionGenerator size(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.size = size;
        return this;
    }

    public CollectionGenerator type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public Collection<?> generate() {
        try {
            return (Collection<?>) type.newInstance();
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
