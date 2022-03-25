package org.instancio.generators.coretypes;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class ShortGenerator extends AbstractRandomNumberGeneratorSpec<Short> {

    public ShortGenerator(final ModelContext<?> context) {
        super(context,
                context.getSettings().get(Setting.SHORT_MIN),
                context.getSettings().get(Setting.SHORT_MAX),
                context.getSettings().get(Setting.SHORT_NULLABLE));
    }

    @Override
    Short generateRandomValue() {
        return random().shortBetween(min, max);
    }
}
