package org.instancio.generator;

import org.instancio.util.Random;

public class StringGenerator implements ValueGenerator<String> {

    @Override
    public String generate() {
        return Random.alphabetic(Random.intBetween(5, 10));
    }
}
