package org.instancio.generators.coretypes;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;

public class ByteGenerator extends AbstractRandomNumberGeneratorSpec<Byte> {

    public ByteGenerator(final ModelContext<?> context) {
        super(context,
                context.getSettings().get(Setting.BYTE_MIN),
                context.getSettings().get(Setting.BYTE_MAX),
                context.getSettings().get(Setting.BYTE_NULLABLE));
    }

    @Override
    Byte generateRandomValue() {
        return random().byteBetween(min, max);
    }
}
