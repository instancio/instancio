package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

import java.time.LocalDate;

public class LocalDateGenerator extends AbstractRandomGenerator<LocalDate> {

    public LocalDateGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public LocalDate generate() {
        return LocalDate.now()
                .plusMonths(random().intBetween(-3650, 3650));
    }
}
