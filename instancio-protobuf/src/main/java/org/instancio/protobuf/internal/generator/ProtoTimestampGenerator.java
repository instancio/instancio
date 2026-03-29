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

import com.google.protobuf.Timestamp;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.util.Constants;
import org.instancio.protobuf.generator.specs.ProtoTimestampGeneratorSpec;

import java.time.Instant;

public class ProtoTimestampGenerator implements Generator<Timestamp>, ProtoTimestampGeneratorSpec {

    private static final int MAX_NANO = 999_999_999;
    private static final Instant DEFAULT_MIN = Constants.DEFAULT_MIN.atZone(Constants.ZONE_OFFSET).toInstant();
    private static final Instant DEFAULT_MAX = Constants.DEFAULT_MAX.atZone(Constants.ZONE_OFFSET).toInstant();

    private Instant min = DEFAULT_MIN;
    private Instant max = DEFAULT_MAX;

    @Override
    public ProtoTimestampGeneratorSpec past() {
        this.min = DEFAULT_MIN;
        this.max = Instant.now().minusSeconds(1);
        return this;
    }

    @Override
    public ProtoTimestampGeneratorSpec future() {
        this.min = Instant.now().plusSeconds(60);
        this.max = DEFAULT_MAX;
        return this;
    }

    @Override
    public ProtoTimestampGeneratorSpec min(final Instant min) {
        this.min = min;
        return this;
    }

    @Override
    public ProtoTimestampGeneratorSpec max(final Instant max) {
        this.max = max;
        return this;
    }

    @Override
    public ProtoTimestampGeneratorSpec range(final Instant min, final Instant max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public Timestamp generate(final Random random) {
        final long minSec = min.getEpochSecond();
        final long maxSec = max.getEpochSecond();
        final long sec = random.longRange(minSec, maxSec);

        final int nano;
        if (sec == minSec && sec == maxSec) {
            nano = random.intRange(
                    Math.min(min.getNano(), max.getNano()),
                    Math.max(min.getNano(), max.getNano()));
        } else if (sec == minSec) {
            nano = random.intRange(min.getNano(), MAX_NANO);
        } else if (sec == maxSec) {
            nano = random.intRange(0, max.getNano());
        } else {
            nano = random.intRange(0, MAX_NANO);
        }

        return Timestamp.newBuilder()
                .setSeconds(sec)
                .setNanos(nano)
                .build();
    }

    @Override
    public Hints hints() {
        return Constants.DO_NOT_MODIFY_HINT;
    }
}
