/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.generator.time;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;

import java.time.Instant;

public class InstantGenerator extends JavaTimeTemporalGenerator<Instant> {

    private static final int MAX_NANO = 999_999_999;

    public InstantGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX);
    }

    @Override
    public String apiMethod() {
        return "instant()";
    }

    @Override
    Instant getLatestPast() {
        return Instant.now().minusSeconds(1);
    }

    @Override
    Instant getEarliestFuture() {
        return Instant.now().plusSeconds(60);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public Instant generate(final Random random) {
        final long sec = random.longRange(min.getEpochSecond(), max.getEpochSecond());
        final int nano;

        if (sec == min.getEpochSecond() && sec == max.getEpochSecond()) {
            nano = random.intRange(Math.min(min.getNano(), max.getNano()), Math.max(min.getNano(), max.getNano()));
        } else if (sec == min.getEpochSecond()) {
            nano = random.intRange(Math.max(min.getNano(), max.getNano()), MAX_NANO);
        } else if (sec == max.getEpochSecond()) {
            nano = random.intRange(0, max.getNano());
        } else {
            nano = random.intRange(0, MAX_NANO);
        }

        return Instant.ofEpochSecond(sec, nano);
    }
}
