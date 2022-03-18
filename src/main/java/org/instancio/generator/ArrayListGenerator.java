package org.instancio.generator;

import java.util.ArrayList;
import java.util.List;

public class ArrayListGenerator<E> implements Generator<List<E>> {
    @Override
    public List<E> generate() {
        return new ArrayList<>();
    }
}
