package org.instancio.generator;

import java.lang.reflect.Array;

public class ArrayGenerator<T> implements Generator<T> {

    private final Class<?> arrayType;
    private final int length;

    public ArrayGenerator(Class<T> arrayType, int length) {
        this.arrayType = arrayType;
        this.length = length;
    }

    @Override
    public T generate() {
        return (T) Array.newInstance(arrayType, length);
    }
}
