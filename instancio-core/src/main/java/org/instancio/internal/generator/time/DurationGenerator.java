/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.generator.specs.DurationSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class DurationGenerator extends AbstractGenerator<Duration> implements DurationSpec {

    private Duration min = Duration.of(Constants.DURATION_MIN_NANOS, ChronoUnit.NANOS);
    private Duration max = Duration.of(Constants.DURATION_MAX_NANOS, ChronoUnit.NANOS);

    private boolean allowZero;

    public DurationGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "duration()";
    }

    @Override
    public DurationGenerator min(final long amount, final TemporalUnit unit) {
        this.min = Duration.of(amount, unit);
        if (min.toNanos() > max.toNanos()) {
            final Long newMax = NumberUtils.calculateNewMax(max.toNanos(), min.toNanos(), Constants.RANGE_ADJUSTMENT_PERCENTAGE);
            max = Duration.of(newMax, ChronoUnit.NANOS);
        }
        return this;
    }

    @Override
    public DurationGenerator max(final long amount, final TemporalUnit unit) {
        max = Duration.of(amount, unit);
        if (max.toNanos() < min.toNanos()) {
            final Long newMin = NumberUtils.calculateNewMin(min.toNanos(), max.toNanos(), Constants.RANGE_ADJUSTMENT_PERCENTAGE);
            min = Duration.of(newMin, ChronoUnit.NANOS);
        }
        return this;
    }

    @Override
    public DurationGenerator of(final long minAmount, final long maxAmount, final TemporalUnit unit) {
        ApiValidator.notNull(unit, "unit must not be null");
        ApiValidator.isTrue(minAmount <= maxAmount,
                "minimum duration amount must be less than or equal to the maximum amount: of(%s, %s, %s)",
                minAmount, maxAmount, unit);

        min(minAmount, unit);
        max(maxAmount, unit);
        return this;
    }

    @Override
    public DurationGenerator allowZero() {
        this.allowZero = true;
        return this;
    }

    @Override
    public DurationGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected Duration tryGenerateNonNull(final Random random) {
        return random.diceRoll(allowZero)
                ? Duration.ZERO
                : Duration.of(random.longRange(min.toNanos(), max.toNanos()), ChronoUnit.NANOS);
    }
}
