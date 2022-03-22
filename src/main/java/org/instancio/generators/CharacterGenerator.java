package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

public class CharacterGenerator implements Generator<Character> {

    @Override
    public Character generate() {
        return Random.character();
    }
}
