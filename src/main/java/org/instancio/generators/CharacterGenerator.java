package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

public class CharacterGenerator extends AbstractGenerator<Character> {

    public CharacterGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Character generate() {
        return random().character();
    }
}
