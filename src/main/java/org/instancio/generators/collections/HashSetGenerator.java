package org.instancio.generators.collections;

import org.instancio.internal.random.RandomProvider;

import java.util.HashSet;

public class HashSetGenerator<T> extends CollectionGenerator<T> {

    public HashSetGenerator(final RandomProvider random) {
        super(random);
        type(HashSet.class);
    }
}
