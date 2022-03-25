package org.instancio.generators.collections;

import org.instancio.internal.model.ModelContext;

import java.util.TreeMap;

public class TreeMapGenerator<K, V> extends MapGenerator<K, V> {

    public TreeMapGenerator(final ModelContext<?> context) {
        super(context);
        type(TreeMap.class);
    }
}
