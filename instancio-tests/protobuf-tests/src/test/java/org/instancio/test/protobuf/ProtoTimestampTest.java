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
package org.instancio.test.protobuf;

import com.google.protobuf.Timestamp;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.protobuf.GenProto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@ExtendWith(InstancioExtension.class)
class ProtoTimestampTest {

    private static final Instant MIN = Instant.ofEpochSecond(1_000_000);
    private static final Instant MAX = Instant.ofEpochSecond(9_000_000);

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range() {
        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().range(MIN, MAX))
                .create();

        assertThat(result.getSeconds()).isBetween(MIN.getEpochSecond(), MAX.getEpochSecond());
        assertThat(result.getNanos()).isBetween(0, 999_999_999);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void rangeSameSecond() {
        final Instant sameSec = Instant.ofEpochSecond(1_000_000, 100_000_000);
        final Instant sameSecMax = Instant.ofEpochSecond(1_000_000, 900_000_000);

        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().range(sameSec, sameSecMax))
                .create();

        assertThat(result.getSeconds()).isEqualTo(1_000_000L);
        assertThat(result.getNanos()).isBetween(100_000_000, 900_000_000);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void rangeNarrow_coversBoundarySeconds() {
        final Instant narrowMin = Instant.ofEpochSecond(5_000_000, 500_000_000);
        final Instant narrowMax = Instant.ofEpochSecond(5_000_001, 500_000_000);

        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().range(narrowMin, narrowMax))
                .create();

        assertThat(result.getSeconds()).isBetween(5_000_000L, 5_000_001L);
        assertThat(result.getNanos()).isBetween(0, 999_999_999);
    }

    @Test
    void min() {
        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().min(MIN))
                .create();

        assertThat(result.getSeconds()).isGreaterThanOrEqualTo(MIN.getEpochSecond());
    }

    @Test
    void max() {
        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().max(MAX))
                .create();

        assertThat(result.getSeconds()).isLessThanOrEqualTo(MAX.getEpochSecond());
    }

    @Test
    void past() {
        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().past())
                .create();

        assertThat(result.getSeconds()).isLessThan(Instant.now().getEpochSecond());
    }

    @Test
    void future() {
        final Timestamp result = Instancio.of(Timestamp.class)
                .generate(all(Timestamp.class), GenProto.timestamp().future())
                .create();

        assertThat(result.getSeconds()).isGreaterThan(Instant.now().getEpochSecond());
    }

}
