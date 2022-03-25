package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class BooleanGenerator extends AbstractRandomGenerator<Boolean> {

    private boolean nullable;

    public BooleanGenerator(final ModelContext<?> context) {
        super(context);
        this.nullable = context.getSettings().get(Setting.BOOLEAN_NULLABLE);
    }

    @Override
    public Boolean generate() {
        return nullable && random().oneInTenTrue() ? null : random().trueOrFalse();
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}
