package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.util.Random;

import java.time.LocalDateTime;

public class LocalDateTimeGenerator implements Generator<LocalDateTime> {

    @Override
    public LocalDateTime generate() {
        return LocalDateTime.now()
                .minusDays(Random.intBetween(0, 3650))
                .minusSeconds(Random.intBetween(0, 60));
    }
}
