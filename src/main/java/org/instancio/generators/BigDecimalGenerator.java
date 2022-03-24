package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

import java.math.BigDecimal;

public class BigDecimalGenerator extends AbstractRandomGenerator<BigDecimal> {

    private static final long MIN = 1;
    private static final long MAX = 10_000;

    public BigDecimalGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public BigDecimal generate() {
        return BigDecimal.valueOf(random().longBetween(MIN, MAX));
    }
}
