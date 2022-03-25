package org.instancio.generators.coretypes;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class FloatGenerator extends AbstractRandomNumberGeneratorSpec<Float> {

    public FloatGenerator(final ModelContext<?> context) {
        super(context,
                context.getSettings().get(Setting.FLOAT_MIN),
                context.getSettings().get(Setting.FLOAT_MAX),
                context.getSettings().get(Setting.FLOAT_NULLABLE));
    }

    @Override
    Float generateRandomValue() {
        return random().floatBetween(min, max);
    }
}
