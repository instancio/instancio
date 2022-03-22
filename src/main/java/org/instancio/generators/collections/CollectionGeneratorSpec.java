package org.instancio.generators.collections;

import org.instancio.GeneratorSpec;

public interface CollectionGeneratorSpec<T> extends GeneratorSpec<T> {

    CollectionGeneratorSpec<T> minSize(int size);

    CollectionGeneratorSpec<T> maxSize(int size);

    CollectionGeneratorSpec<T> type(Class<?> type);

}
