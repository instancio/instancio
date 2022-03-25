package org.instancio.generators.collections;

import org.instancio.internal.model.ModelContext;

import java.util.HashSet;

public class HashSetGenerator<T> extends CollectionGenerator<T> {

    public HashSetGenerator(final ModelContext<?> context) {
        super(context);
        type(HashSet.class);
    }
}
