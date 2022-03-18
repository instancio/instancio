package org.instancio.generator;

import org.instancio.util.Random;

public class CharacterGenerator implements Generator<Character> {

    @Override
    public Character generate() {
        return Random.character();
    }
}
