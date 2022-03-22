package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

public class DoubleGenerator implements Generator<Double> {

    @Override
    public Double generate() {
        return Random.doubleBetween(Double.MIN_VALUE, Double.MAX_VALUE);
    }
}
