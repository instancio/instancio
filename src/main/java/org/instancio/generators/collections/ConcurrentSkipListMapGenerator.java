package org.instancio.generators.collections;

import org.instancio.internal.random.RandomProvider;

import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapGenerator<K, V> extends MapGenerator<K, V> {

    public ConcurrentSkipListMapGenerator(final RandomProvider random) {
        super(random);
        type(ConcurrentSkipListMap.class);
    }
}
