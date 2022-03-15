package org.instancio.generator;

import java.util.TreeMap;

public class TreeMapGenerator<K, V> implements ValueGenerator<TreeMap<K, V>> {

    @Override
    @SuppressWarnings("SortedCollectionWithNonComparableKeys")
    public TreeMap<K, V> generate() {
        return new TreeMap<>();
    }
}
