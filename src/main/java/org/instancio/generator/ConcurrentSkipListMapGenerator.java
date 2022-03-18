package org.instancio.generator;

import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapGenerator<K, V> implements Generator<ConcurrentSkipListMap<K, V>> {

    @Override
    @SuppressWarnings("SortedCollectionWithNonComparableKeys")
    public ConcurrentSkipListMap<K, V> generate() {
        return new ConcurrentSkipListMap<>();
    }
}
