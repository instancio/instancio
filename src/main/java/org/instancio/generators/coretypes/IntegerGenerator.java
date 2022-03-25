package org.instancio.generators.coretypes;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class IntegerGenerator extends AbstractRandomNumberGeneratorSpec<Integer> {

    public IntegerGenerator(final ModelContext<?> context) {
        super(context,
                context.getSettings().get(Setting.INTEGER_MIN),
                context.getSettings().get(Setting.INTEGER_MAX),
                context.getSettings().get(Setting.INTEGER_NULLABLE));
    }

    @Override
    Integer generateRandomValue() {
        return random().intBetween(min, max);
    }

}
