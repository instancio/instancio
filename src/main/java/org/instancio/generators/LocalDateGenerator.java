package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

import java.time.LocalDate;

public class LocalDateGenerator implements Generator<LocalDate> {

    @Override
    public LocalDate generate() {
        return LocalDate.now()
                .minusDays(Random.intBetween(0, 3650));
    }
}
