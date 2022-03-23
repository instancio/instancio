package org.instancio.generators.collections;

import org.instancio.internal.random.RandomProvider;

import java.util.TreeMap;

public class TreeMapGenerator<K, V> extends MapGenerator<K, V> {

    public TreeMapGenerator(final RandomProvider random) {
        super(random);
        type(TreeMap.class);
    }
}
