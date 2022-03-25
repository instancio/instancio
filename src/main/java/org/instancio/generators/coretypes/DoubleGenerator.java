package org.instancio.generators.coretypes;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class DoubleGenerator extends AbstractRandomNumberGeneratorSpec<Double> {

    public DoubleGenerator(final ModelContext<?> context) {
        super(context,
                context.getSettings().get(Setting.DOUBLE_MIN),
                context.getSettings().get(Setting.DOUBLE_MAX),
                context.getSettings().get(Setting.DOUBLE_NULLABLE));
    }

    @Override
    Double generateRandomValue() {
        return random().doubleBetween(min, max);
    }
}
