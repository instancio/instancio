package org.instancio.generators.collections;

import org.instancio.internal.random.RandomProvider;

import java.util.TreeSet;

public class TreeSetGenerator<T> extends CollectionGenerator<T> {

    public TreeSetGenerator(final RandomProvider random) {
        super(random);
        type(TreeSet.class);
    }
}
