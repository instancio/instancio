package org.instancio.generator;

import java.util.HashSet;
import java.util.Set;

public class HashSetGenerator<E> implements ValueGenerator<Set<E>> {
    @Override
    public Set<E> generate() {
        return new HashSet<>();
    }
}
