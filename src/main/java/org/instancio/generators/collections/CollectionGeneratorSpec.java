package org.instancio.generators.collections;

import org.instancio.GeneratorSpec;

import java.util.Collection;

public interface CollectionGeneratorSpec<T> extends GeneratorSpec<Collection<T>> {

    CollectionGeneratorSpec<T> minSize(int size);

    CollectionGeneratorSpec<T> maxSize(int size);

    CollectionGeneratorSpec<T> type(Class<?> type);

}
