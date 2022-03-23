package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class OneOfArrayGenerator<T> extends AbstractRandomGenerator<T> implements OneOfArrayGeneratorSpec<T> {

    private T[] values;

    public OneOfArrayGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public OneOfArrayGeneratorSpec<T> oneOf(final T[] values) {
        this.values = values;
        return this;
    }

    @Override
    public T generate() {
        Verify.notEmpty(values, "Array must have at least one element");
        return random().from(values);
    }
}
