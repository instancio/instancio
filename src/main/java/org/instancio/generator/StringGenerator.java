package org.instancio.generator;

import org.instancio.util.Random;

public class StringGenerator implements Generator<String> {

    @Override
    public String generate() {
        return Random.alphabetic(Random.intBetween(5, 10));
    }
}
