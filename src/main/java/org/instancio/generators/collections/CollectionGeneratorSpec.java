package org.instancio.generators.collections;

import org.instancio.GeneratorSpec;

public interface CollectionGeneratorSpec<T> extends GeneratorSpec<T> {

    //  - collections.elements.nullable=true|false
    //  - collections.nullable=true|false
    //  - collections.min.size=2
    //  - collections.max.size=8
    //  - collections.map.java.util.List=java.util.ArrayList
    //  - collections.map.java.util.Collection=java.util.HashSet

    CollectionGeneratorSpec<T> minSize(int size);

    CollectionGeneratorSpec<T> maxSize(int size);

    CollectionGeneratorSpec<T> type(Class<?> type);

}
