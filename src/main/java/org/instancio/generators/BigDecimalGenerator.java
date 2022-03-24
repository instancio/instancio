package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

import java.math.BigDecimal;

// TODO
public class BigDecimalGenerator extends AbstractRandomGenerator<BigDecimal> {

    public BigDecimalGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public BigDecimal generate() {
        return BigDecimal.valueOf(random().longBetween(Long.MIN_VALUE, Long.MAX_VALUE));
    }
}
