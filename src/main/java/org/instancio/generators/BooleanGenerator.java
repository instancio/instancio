package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class BooleanGenerator extends AbstractGenerator<Boolean> {

    public BooleanGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Boolean generate() {
        return random().trueOrFalse();
    }
}
