package org.instancio.generators.collections;

import org.instancio.exception.InstancioException;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratorSettings;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionGenerator<T> extends AbstractRandomGenerator<Collection<T>> implements CollectionGeneratorSpec<T> {

    private int minSize = 2;
    private int maxSize = 6;
    private Class<?> type = ArrayList.class;

    public CollectionGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public CollectionGeneratorSpec<T> minSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.minSize = size;
        this.maxSize = Math.max(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> maxSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.maxSize = size;
        this.minSize = Math.min(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;

    }

    @Override
    public Collection<T> generate() {
        try {
            return (Collection<T>) type.newInstance(); // TODO
        } catch (Exception ex) {
            throw new InstancioException(String.format("Error creating instance of: %s", type), ex);
        }
    }

    @Override
    public GeneratorSettings getSettings() {
        return GeneratorSettings.builder()
                .dataStructureSize(random().intBetween(minSize, maxSize + 1))
                .ignoreChildren(false)
                .build();
    }
}
