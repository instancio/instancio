package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class CharacterGenerator extends AbstractRandomGenerator<Character> {

    private boolean nullable;

    public CharacterGenerator(final ModelContext<?> context) {
        super(context);
        this.nullable = context.getSettings().get(Setting.CHARACTER_NULLABLE);
    }

    @Override
    public Character generate() {
        return nullable && random().oneInTenTrue() ? null : random().character();
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}
