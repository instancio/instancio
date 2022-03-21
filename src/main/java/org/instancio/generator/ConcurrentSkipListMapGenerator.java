package org.instancio.generator;

import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapGenerator extends MapGenerator {

    public ConcurrentSkipListMapGenerator() {
        super.type(ConcurrentSkipListMap.class);
    }
}
