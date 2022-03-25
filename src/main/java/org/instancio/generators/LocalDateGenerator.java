package org.instancio.generators;

import org.instancio.internal.model.ModelContext;

import java.time.LocalDate;

public class LocalDateGenerator extends AbstractRandomGenerator<LocalDate> {

    public LocalDateGenerator(final ModelContext<?> context) {
        super(context);
    }

    @Override
    public LocalDate generate() {
        return LocalDate.now()
                .plusMonths(random().intBetween(-3650, 3650));
    }
}
