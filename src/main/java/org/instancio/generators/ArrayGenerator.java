package org.instancio.generators;

import org.instancio.internal.GeneratorSettings;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.lang.reflect.Array;

public class ArrayGenerator<T> extends AbstractRandomGenerator<T> implements ArrayGeneratorSpec<T> {

    private int minLength = 2;
    private int maxLength = 6;
    private Class<?> componentType;

    public ArrayGenerator(final RandomProvider random) {
        super(random);
    }

    public ArrayGenerator(final RandomProvider random, final Class<?> componentType) {
        super(random);
        this.componentType = Verify.notNull(componentType, "Type must not be null");
    }

    @Override
    public ArrayGeneratorSpec<T> minLength(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.minLength = length;
        this.maxLength = Math.max(maxLength, minLength);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> maxLength(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, maxLength);
        return this;
    }

    public ArrayGenerator<T> type(final Class<?> type) {
        this.componentType = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T generate() {
        Verify.notNull(componentType, "Array component type is null");
        return (T) Array.newInstance(componentType, random().intBetween(minLength, maxLength + 1));
    }

    @Override
    public GeneratorSettings getSettings() {
        return GeneratorSettings.builder()
                .ignoreChildren(false)
                .build();
    }
}
