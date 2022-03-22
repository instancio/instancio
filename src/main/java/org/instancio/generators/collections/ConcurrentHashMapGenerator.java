package org.instancio.generators.collections;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapGenerator extends MapGenerator {

    public ConcurrentHashMapGenerator() {
        super.type(ConcurrentHashMap.class);
    }

}
