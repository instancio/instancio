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
package org.instancio.test.jpa;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.FloatAndDoubleWithScaleAndPrecision;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithDefaultPrecision;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithPrecision;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithPrecisionScale;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithPrecisionScaleAndDecimalMinMax;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithPrecisionScaleAndMinMax;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithPrecisionScaleAndNegative;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithPrecisionScaleAndPositive;
import org.instancio.test.pojo.jpa.ColumnScaleAndPrecisionJPA.WithScale;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.JPA)
@ExtendWith(InstancioExtension.class)
class ColumnScaleAndPrecisionJPATest {

    @RepeatedTest(Constants.SAMPLE_SIZE_DDD)
    void floatAndDoubleWithScaleAndPrecision() {
        final FloatAndDoubleWithScaleAndPrecision result = Instancio.create(FloatAndDoubleWithScaleAndPrecision.class);

        assertThat(result.getF())
                .isBetween(0f, 99.99f)
                .asString()
                .matches("^\\d{2}\\.\\d{1,2}$");

        assertThat(result.getD())
                .isBetween(100d, 999.999d)
                .asString()
                .matches("^\\d{3}\\.\\d{1,3}$");
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withDefaultPrecision() {
        final WithDefaultPrecision result = Instancio.create(WithDefaultPrecision.class);

        // default precision of zero should be ignored
        assertThat(result.getD()).isBetween(
                new BigDecimal("1"),
                new BigDecimal("10000"));
    }

    /**
     * With default scale of zero.
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void withPrecisionAndDefaultScale() {
        final WithPrecision result = Instancio.create(WithPrecision.class);

        assertThat(result.getD1().precision()).isEqualTo(1);
        assertThat(result.getD1()).hasScaleOf(0);

        assertThat(result.getD2().precision()).isEqualTo(2);
        assertThat(result.getD2()).hasScaleOf(0);

        assertThat(result.getD3().precision()).isEqualTo(15);
        assertThat(result.getD3()).hasScaleOf(0);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void withScale() {
        final WithScale result = Instancio.create(WithScale.class);

        assertThat(result.getD1())
                .hasScaleOf(-2)
                .isBetween(
                        new BigDecimal("10"),
                        new BigDecimal("10000"));

        assertThat(result.getD2())
                .hasScaleOf(9)
                .isBetween(
                        new BigDecimal("1"),
                        new BigDecimal("10000"));
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void withPrecisionAndScale() {
        final WithPrecisionScale result = Instancio.create(WithPrecisionScale.class);

        assertThat(result.getD1().precision()).isEqualTo(2);
        assertThat(result.getD1()).hasScaleOf(1);

        assertThat(result.getD2().precision()).isEqualTo(6);
        assertThat(result.getD2()).hasScaleOf(2);

        assertThat(result.getD3().precision()).isEqualTo(3);
        assertThat(result.getD3()).hasScaleOf(-10);

        assertThat(result.getD4().precision()).isEqualTo(10);
        assertThat(result.getD4()).hasScaleOf(10);

        assertThat(result.getD5().precision()).isEqualTo(10);
        assertThat(result.getD5()).hasScaleOf(15);
    }

    /**
     * {@code @Column(precision)} is not honoured when used
     * with {@code @DecimalMin} or {@code @DecimalMax}.
     */
    @FeatureTag(Feature.UNSUPPORTED)
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withPrecisionScaleAndDecimalMinMax() {
        final WithPrecisionScaleAndDecimalMinMax result = Instancio.create(WithPrecisionScaleAndDecimalMinMax.class);

        final BigDecimal expectedMin = new BigDecimal("1.1");
        final BigDecimal expectedMax = new BigDecimal("1.6");

        assertThat(result.getD1())
                .hasScaleOf(3)
                .isBetween(expectedMin, expectedMax);

        assertThat(result.getD2())
                .hasScaleOf(3)
                .isBetween(expectedMin, expectedMax);
    }

    /**
     * {@code @Column(precision)} is not honoured when used
     * with {@code @Min} or {@code @Max}.
     */
    @FeatureTag(Feature.UNSUPPORTED)
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withPrecisionScaleAndMinMax() {
        final WithPrecisionScaleAndMinMax result = Instancio.create(WithPrecisionScaleAndMinMax.class);

        assertThat(result.getD())
                .hasScaleOf(4)
                .isBetween(
                        new BigDecimal("2"),
                        new BigDecimal("3"));
    }

    /**
     * {@code @Column(precision)} is not honoured when used with {@code @Negative}.
     */
    @FeatureTag(Feature.UNSUPPORTED)
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withPrecisionScaleAndNegative() {
        final WithPrecisionScaleAndNegative result = Instancio.create(WithPrecisionScaleAndNegative.class);

        assertThat(result.getD())
                .isNegative()
                .hasScaleOf(10);

        assertThat(result.getD().precision()).isGreaterThanOrEqualTo(10);
    }

    /**
     * {@code @Column(precision)} is not honoured when used with {@code @Positive}.
     */
    @FeatureTag(Feature.UNSUPPORTED)
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withPrecisionScaleAndPositive() {
        final WithPrecisionScaleAndPositive result = Instancio.create(WithPrecisionScaleAndPositive.class);

        assertThat(result.getD())
                .isPositive()
                .hasScaleOf(10);

        assertThat(result.getD().precision()).isGreaterThanOrEqualTo(10);
    }
}
