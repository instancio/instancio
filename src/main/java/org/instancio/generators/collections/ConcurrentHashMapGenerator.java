package org.instancio.generators.collections;

import org.instancio.internal.random.RandomProvider;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapGenerator<K, V> extends MapGenerator<K, V> {

    public ConcurrentHashMapGenerator(final RandomProvider random) {
        super(random);
        type(ConcurrentHashMap.class);
    }

}
