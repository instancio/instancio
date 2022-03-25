package org.instancio.generators;

import org.instancio.internal.model.ModelContext;

import java.math.BigDecimal;

public class BigDecimalGenerator extends AbstractRandomGenerator<BigDecimal> {

    private static final long MIN = 1;
    private static final long MAX = 10_000;

    public BigDecimalGenerator(final ModelContext<?> context) {
        super(context);
    }

    @Override
    public BigDecimal generate() {
        return BigDecimal.valueOf(random().longBetween(MIN, MAX));
    }
}
