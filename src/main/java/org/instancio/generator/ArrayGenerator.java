package org.instancio.generator;

import org.instancio.util.Verify;

import java.lang.reflect.Array;

public class ArrayGenerator implements Generator<Object> {

    private int length = 2;
    private Class<?> componentType;

    public ArrayGenerator() {
    }

    ArrayGenerator(final Class<?> componentType) {
        this.componentType = Verify.notNull(componentType, "Type must not be null");
    }

    public ArrayGenerator type(final Class<?> type) {
        this.componentType = Verify.notNull(type, "Type must not be null");
        return this;
    }

    public ArrayGenerator length(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: " + length);
        this.length = length;
        return this;
    }

    @Override
    public Object generate() {
        Verify.notNull(componentType, "Array component type is null");
        return Array.newInstance(componentType, length);
    }

    @Override
    public GeneratorSettings getSettings() {
        return GeneratorSettings.builder()
                .dataStructureSize(length)
                .ignoreChildren(false)
                .build();
    }
}
