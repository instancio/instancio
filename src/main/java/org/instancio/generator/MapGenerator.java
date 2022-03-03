package org.instancio.generator;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator<K, V> implements ValueGenerator<Map<K, V>> {
    @Override
    public Map<K, V> generate() {
        return new HashMap<>();
    }
}
