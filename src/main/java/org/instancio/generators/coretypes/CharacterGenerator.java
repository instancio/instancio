package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.random.RandomProvider;

public class CharacterGenerator extends AbstractRandomGenerator<Character> {

    public CharacterGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public Character generate() {
        return random().character();
    }
}
