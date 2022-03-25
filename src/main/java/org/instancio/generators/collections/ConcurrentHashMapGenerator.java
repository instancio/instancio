package org.instancio.generators.collections;

import org.instancio.internal.model.ModelContext;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapGenerator<K, V> extends MapGenerator<K, V> {

    public ConcurrentHashMapGenerator(final ModelContext<?> context) {
        super(context);
        type(ConcurrentHashMap.class);
    }

}
