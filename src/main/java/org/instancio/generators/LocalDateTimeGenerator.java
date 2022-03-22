package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;

import java.time.LocalDateTime;

public class LocalDateTimeGenerator extends AbstractGenerator<LocalDateTime> {

    public LocalDateTimeGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public LocalDateTime generate() {
        return LocalDateTime.now()
                .minusDays(random().intBetween(0, 3650))
                .minusSeconds(random().intBetween(0, 60));
    }
}
