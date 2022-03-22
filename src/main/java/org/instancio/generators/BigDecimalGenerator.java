package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

import java.math.BigDecimal;

public class BigDecimalGenerator implements Generator<BigDecimal> {

    @Override
    public BigDecimal generate() {
        return BigDecimal.valueOf(Random.longBetween(Long.MIN_VALUE, Long.MAX_VALUE));
    }
}
