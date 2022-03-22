package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;

public class BooleanGenerator extends AbstractRandomGenerator<Boolean> {

    public BooleanGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Boolean generate() {
        return random().trueOrFalse();
    }
}
