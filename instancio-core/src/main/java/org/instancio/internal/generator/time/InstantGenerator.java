/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.generator.specs.InstantGeneratorAsSpec;
import org.instancio.generator.specs.InstantSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Constants;
import org.instancio.support.Global;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

public class InstantGenerator extends JavaTimeTemporalGenerator<Instant>
        implements InstantGeneratorAsSpec, InstantSpec {

    private static final int MAX_NANO = 999_999_999;
    static final Instant DEFAULT_MIN = Constants.DEFAULT_MIN.atZone(Constants.ZONE_OFFSET).toInstant();
    static final Instant DEFAULT_MAX = Constants.DEFAULT_MAX.atZone(Constants.ZONE_OFFSET).toInstant();

    public InstantGenerator() {
        this(Global.generatorContext());
    }

    public InstantGenerator(final GeneratorContext context) {
        super(context, DEFAULT_MIN, DEFAULT_MAX);
    }

    @Override
    public String apiMethod() {
        return "instant()";
    }

    @Override
    public InstantGenerator past() {
        super.past();
        return this;
    }

    @Override
    public InstantGenerator future() {
        super.future();
        return this;
    }

    @Override
    public InstantGenerator min(final Instant min) {
        super.min(min);
        return this;
    }

    @Override
    public InstantGenerator max(final Instant max) {
        super.max(max);
        return this;
    }

    @Override
    public InstantGenerator range(final Instant min, final Instant max) {
        super.range(min, max);
        return this;
    }

    @Override
    public InstantGenerator truncatedTo(final TemporalUnit unit) {
        super.truncatedTo(unit);
        return this;
    }

    @Override
    public InstantGenerator nullable() {
        super.nullable();
        return this;
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
        ApiValidator.validateStartEnd(min, max);
    }

    @Override
    public Instant tryGenerateNonNull(final Random random) {
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

        final Instant result = Instant.ofEpochSecond(sec, nano);
        return truncateTo == null ? result : result.truncatedTo(truncateTo);
    }
}
