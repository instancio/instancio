package org.instancio.generator;

import org.instancio.util.Random;

import java.math.BigDecimal;

public class BigDecimalGenerator implements ValueGenerator<BigDecimal> {

    @Override
    public BigDecimal generate() {
        return BigDecimal.valueOf(Random.longBetween(Long.MIN_VALUE, Long.MAX_VALUE));
    }
}
