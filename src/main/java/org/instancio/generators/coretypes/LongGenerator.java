package org.instancio.generators.coretypes;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class LongGenerator extends AbstractRandomNumberGeneratorSpec<Long> {

    public LongGenerator(final ModelContext<?> context) {
        super(context,
                context.getSettings().get(Setting.LONG_MIN),
                context.getSettings().get(Setting.LONG_MAX),
                context.getSettings().get(Setting.LONG_NULLABLE));
    }

    @Override
    Long generateRandomValue() {
        return random().longBetween(min, max);
    }
}
