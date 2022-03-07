package org.instancio.generator;

import java.util.ArrayList;
import java.util.List;

public class ListGenerator<E> implements ValueGenerator<List<E>> {
    @Override
    public List<E> generate() {
        return new ArrayList<>();
    }
}
