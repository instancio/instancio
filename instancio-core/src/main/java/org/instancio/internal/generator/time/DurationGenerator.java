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
import org.instancio.generator.specs.DurationGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class DurationGenerator extends AbstractGenerator<Duration> implements DurationGeneratorSpec {

    private static final long DEFAULT_MIN_AMOUNT = 1;
    private static final long DEFAULT_MAX_AMOUNT = 1_000_000_000_000_000L;
    private static final TemporalUnit DEFAULT_UNIT = ChronoUnit.NANOS;

    private long minAmount = DEFAULT_MIN_AMOUNT;
    private long maxAmount = DEFAULT_MAX_AMOUNT;
    private TemporalUnit unit = DEFAULT_UNIT;
    private boolean allowZero;

    public DurationGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "duration()";
    }

    @Override
    public DurationGeneratorSpec of(final long minAmount, final long maxAmount, final TemporalUnit unit) {
        ApiValidator.notNull(unit, "Unit must not be null");
        ApiValidator.isTrue(minAmount <= maxAmount,
                "Minimum duration amount must be less than or equal to the maximum amount: of(%s, %s, %s)",
                minAmount, maxAmount, unit);

        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.unit = unit;
        return this;
    }

    @Override
    public DurationGeneratorSpec allowZero() {
        this.allowZero = true;
        return this;
    }

    @Override
    public Duration generate(final Random random) {
        return random.diceRoll(allowZero)
                ? Duration.ZERO
                : Duration.of(random.longRange(minAmount, maxAmount), unit);
    }
}
