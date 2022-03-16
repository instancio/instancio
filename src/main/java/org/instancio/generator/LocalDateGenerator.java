package org.instancio.generator;

import org.instancio.util.Random;

import java.time.LocalDate;

public class LocalDateGenerator implements ValueGenerator<LocalDate> {

    @Override
    public LocalDate generate() {
        return LocalDate.now()
                .minusDays(Random.intBetween(0, 3650));
    }
}
