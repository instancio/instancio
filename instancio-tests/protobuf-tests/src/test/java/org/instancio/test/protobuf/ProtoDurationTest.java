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

import com.google.protobuf.Duration;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.protobuf.GenProto;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@ExtendWith(InstancioExtension.class)
class ProtoDurationTest {

    private static final long MIN_SECONDS = 60;
    private static final long MAX_SECONDS = 3600;

    private static final java.time.Duration MIN = java.time.Duration.ofSeconds(MIN_SECONDS);
    private static final java.time.Duration MAX = java.time.Duration.ofSeconds(MAX_SECONDS);

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range_positive() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(MIN, MAX))
                .create();

        assertThat(result.getDuration().getSeconds()).isBetween(MIN_SECONDS, MAX_SECONDS);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range_negative() {
        final long minSeconds = -100;
        final long maxSeconds = -1;

        final java.time.Duration min = java.time.Duration.ofSeconds(minSeconds);
        final java.time.Duration max = java.time.Duration.ofSeconds(maxSeconds);

        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(min, max))
                .create();

        assertThat(result.getDuration().getSeconds()).isBetween(minSeconds, maxSeconds);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void rangeSameSecond() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(MIN, MIN))
                .create();

        assertThat(result.getDuration().getSeconds()).isBetween(MIN_SECONDS, MAX_SECONDS);
    }

    @Test
    void min() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().min(MIN))
                .create();

        assertThat(result.getDuration().getSeconds()).isGreaterThanOrEqualTo(MIN_SECONDS);
    }

    @Test
    void max() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().max(MAX))
                .create();

        assertThat(result.getDuration().getSeconds()).isLessThanOrEqualTo(MAX_SECONDS);
    }

    @Test
    void positive() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().positive())
                .create();

        assertThat(result.getDuration().getSeconds()).isPositive();
    }

    @Test
    void negative() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().negative())
                .create();

        assertThat(result.getDuration().getSeconds()).isNegative();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range_endingAtZero_nanosMustBeNonPositive() {
        final java.time.Duration rangeMin = java.time.Duration.ofSeconds(-2);
        final java.time.Duration rangeMax = java.time.Duration.ZERO;

        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(rangeMin, rangeMax))
                .create();

        final Duration d = result.getDuration();
        assertThat(d.getSeconds()).isLessThanOrEqualTo(0L);
        if (d.getSeconds() == 0) {
            assertThat(d.getNanos()).isLessThanOrEqualTo(0);
        }
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range_multiSecondSpan_shouldRespectNanoBounds() {
        final java.time.Duration rangeMin = java.time.Duration.ofSeconds(1, 500_000_000);
        final java.time.Duration rangeMax = java.time.Duration.ofSeconds(2, 300_000_000);

        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(rangeMin, rangeMax))
                .create();

        final Duration d = result.getDuration();
        final java.time.Duration actual = java.time.Duration.ofSeconds(d.getSeconds(), d.getNanos());
        assertThat(actual).isBetween(rangeMin, rangeMax);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range_multiSecondSpan_negative_shouldRespectNanoBounds() {
        final java.time.Duration rangeMin = java.time.Duration.ofSeconds(-5, 500_000_000);
        final java.time.Duration rangeMax = java.time.Duration.ofSeconds(-2, 300_000_000);

        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(rangeMin, rangeMax))
                .create();

        final Duration d = result.getDuration();
        assertThat(d.getSeconds()).isBetween(-5L, -2L);
        assertThat(d.getNanos()).isLessThanOrEqualTo(0);

        if (d.getSeconds() == -5) {
            assertThat(d.getNanos()).isGreaterThanOrEqualTo(-500_000_000);
        }
        if (d.getSeconds() == -2) {
            assertThat(d.getNanos()).isLessThanOrEqualTo(-300_000_000);
        }
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void rangeSameSecond_negative() {
        final java.time.Duration min = java.time.Duration.ofSeconds(-5, 100_000_000);
        final java.time.Duration max = java.time.Duration.ofSeconds(-5, 900_000_000);

        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .generate(all(Duration.class), GenProto.duration().range(min, max))
                .create();

        assertThat(result.getDuration().getSeconds()).isEqualTo(-5);
        assertThat(result.getDuration().getNanos()).isBetween(-900_000_000, -100_000_000);
    }
}
