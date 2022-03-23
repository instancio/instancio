package org.instancio.generators.collections;

import org.instancio.GeneratorSpec;

import java.util.Map;

public interface MapGeneratorSpec<K, V> extends GeneratorSpec<Map<K, V>> {

    MapGeneratorSpec<K, V> minSize(int size);

    MapGeneratorSpec<K, V> maxSize(int size);

    MapGeneratorSpec<K, V> type(Class<?> type);

}
