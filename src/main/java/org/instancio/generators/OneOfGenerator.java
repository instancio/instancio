package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class OneOfGenerator<T> extends AbstractRandomGenerator<T> implements OneOfGeneratorSpec<T> {

    private T[] values;

    public OneOfGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public OneOfGeneratorSpec<T> oneOf(final T[] values) {
        this.values = values;
        return this;
    }

    @Override
    public T generate() {
        return random().from(values);
    }
}
