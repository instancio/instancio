package org.instancio.generators;

import org.instancio.internal.model.ModelContext;

import java.time.LocalDateTime;

public class LocalDateTimeGenerator extends AbstractRandomGenerator<LocalDateTime> {

    public LocalDateTimeGenerator(final ModelContext<?> context) {
        super(context);
    }

    @Override
    public LocalDateTime generate() {
        return LocalDateTime.now()
                .plusDays(random().intBetween(-3650, 3650))
                .minusSeconds(random().intBetween(0, 60));
    }
}
