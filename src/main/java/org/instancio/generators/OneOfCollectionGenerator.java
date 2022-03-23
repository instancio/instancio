package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.util.Collection;

public class OneOfCollectionGenerator<T> extends AbstractRandomGenerator<T> implements OneOfCollectionGeneratorSpec<T> {

    private Collection<T> values;

    public OneOfCollectionGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public OneOfCollectionGeneratorSpec<T> oneOf(final Collection<T> values) {
        this.values = values;
        return this;
    }

    @Override
    public T generate() {
        Verify.notEmpty(values, "Array must have at least one element");
        return random().from(values);
    }
}
