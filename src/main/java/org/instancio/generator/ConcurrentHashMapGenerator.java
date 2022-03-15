package org.instancio.generator;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapGenerator<K, V> implements ValueGenerator<ConcurrentHashMap<K, V>> {

    @Override
    public ConcurrentHashMap<K, V> generate() {
        return new ConcurrentHashMap<>();
    }
}
