package org.instancio.generators.collections;

import org.instancio.exception.InstancioException;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratorSettings;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator<K, V> extends AbstractRandomGenerator<Map<K, V>> implements MapGeneratorSpec<K, V> {

    private int minSize = 2;
    private int maxSize = 6;
    private Class<?> type = HashMap.class;

    public MapGenerator(final RandomProvider random) {
        super(random);
    }

    public MapGeneratorSpec<K, V> type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> minSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.minSize = size;
        this.maxSize = Math.max(maxSize, minSize);
        return this;
    }

    @Override
    public MapGeneratorSpec<K, V> maxSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.maxSize = size;
        this.minSize = Math.min(minSize, maxSize);
        return this;
    }

    @Override
    public Map<K, V> generate() {
        try {
            return (Map<K, V>) type.newInstance();
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
