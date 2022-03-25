package org.instancio.generators.collections;

import org.instancio.internal.model.ModelContext;

import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapGenerator<K, V> extends MapGenerator<K, V> {

    public ConcurrentSkipListMapGenerator(final ModelContext<?> context) {
        super(context);
        type(ConcurrentSkipListMap.class);
    }
}
