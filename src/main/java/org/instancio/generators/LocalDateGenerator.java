package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

import java.time.LocalDate;

public class LocalDateGenerator extends AbstractGenerator<LocalDate> {

    public LocalDateGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public LocalDate generate() {
        return LocalDate.now()
                .minusDays(random().intBetween(0, 3650));
    }
}
