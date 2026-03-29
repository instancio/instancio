/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.protobuf.internal.generator;

import com.google.protobuf.Duration;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.util.Constants;
import org.instancio.protobuf.generator.specs.ProtoDurationGeneratorSpec;

public class ProtoDurationGenerator implements Generator<Duration>, ProtoDurationGeneratorSpec {

    private static final int MAX_NANO = 999_999_999;

    private static final java.time.Duration DEFAULT_MAX = java.time.Duration.ofSeconds(
            Constants.DEFAULT_MAX.atZone(Constants.ZONE_OFFSET).toInstant().getEpochSecond());

    private java.time.Duration min = java.time.Duration.ZERO;
    private java.time.Duration max = DEFAULT_MAX;

    @Override
    public ProtoDurationGeneratorSpec positive() {
        this.min = java.time.Duration.ZERO;
        this.max = DEFAULT_MAX;
        return this;
    }

    @Override
    public ProtoDurationGeneratorSpec negative() {
        this.min = DEFAULT_MAX.negated();
        this.max = java.time.Duration.ofSeconds(-1);
        return this;
    }

    @Override
    public ProtoDurationGeneratorSpec min(final java.time.Duration min) {
        this.min = min;
        return this;
    }

    @Override
    public ProtoDurationGeneratorSpec max(final java.time.Duration max) {
        this.max = max;
        return this;
    }

    @Override
    public ProtoDurationGeneratorSpec range(final java.time.Duration min, final java.time.Duration max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public Duration generate(final Random random) {
        final long minSec = min.getSeconds();
        final long maxSec = max.getSeconds();
        final long sec = random.longRange(minSec, maxSec);

        final int nano;
        if (sec == minSec && sec == maxSec) {
            // Same second: narrow the nano range accordingly
            final int minNano = toProtoNano(min);
            final int maxNano = toProtoNano(max);
            nano = random.intRange(Math.min(minNano, maxNano), Math.max(minNano, maxNano));
        } else if (sec == minSec) {
            final int minNano = toProtoNano(min);
            final int fullNano = sec >= 0 ? MAX_NANO : 0;
            nano = random.intRange(Math.min(minNano, fullNano), Math.max(minNano, fullNano));
        } else if (sec == maxSec) {
            final int maxNano = toProtoNano(max);
            final int fullNano = sec >= 0 ? 0 : -MAX_NANO;
            nano = random.intRange(Math.min(fullNano, maxNano), Math.max(fullNano, maxNano));
        } else if (sec >= 0) {
            nano = random.intRange(0, MAX_NANO);
        } else {
            nano = random.intRange(-MAX_NANO, 0);
        }

        return Duration.newBuilder()
                .setSeconds(sec)
                .setNanos(nano)
                .build();
    }

    /**
     * Proto {@code Duration.nanos} must have the same sign as {@code seconds}
     * or be zero. Therefore, negate the nano part for a negative duration.
     */
    private static int toProtoNano(final java.time.Duration d) {
        return d.isNegative() ? -d.getNano() : d.getNano();
    }

    @Override
    public Hints hints() {
        return Constants.DO_NOT_MODIFY_HINT;
    }
}
