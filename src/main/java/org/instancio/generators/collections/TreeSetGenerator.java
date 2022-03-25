package org.instancio.generators.collections;

import org.instancio.internal.model.ModelContext;

import java.util.TreeSet;

public class TreeSetGenerator<T> extends CollectionGenerator<T> {

    public TreeSetGenerator(final ModelContext<?> context) {
        super(context);
        type(TreeSet.class);
    }
}
