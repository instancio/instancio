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
package org.instancio.test.pojo.jpa;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class ColumnScaleAndPrecisionJPA {

    @Data
    public static class WithDefaultPrecision {
        @Column
        private BigDecimal d;
    }

    @Data
    public static class WithPrecision {
        @Column(precision = 1)
        private BigDecimal d1;

        @Column(precision = 2)
        private BigDecimal d2;

        @Column(precision = 15)
        private BigDecimal d3;
    }

    @Data
    public static class WithScale {
        @Column(scale = -2)
        private BigDecimal d1;

        @Column(scale = 9)
        private BigDecimal d2;
    }

    @Data
    public static class WithPrecisionScale {
        @Column(precision = 2, scale = 1)
        private BigDecimal d1;

        @Column(precision = 6, scale = 2)
        private BigDecimal d2;

        @Column(precision = 3, scale = -10)
        private BigDecimal d3;

        @Column(precision = 10, scale = 10)
        private BigDecimal d4;

        @Column(precision = 10, scale = 15)
        private BigDecimal d5;
    }

    @Data
    public static class WithPrecisionScaleAndDecimalMinMax {
        // DecimalMin/Max should take precedence over precision
        @Column(precision = 6, scale = 3)
        @DecimalMin("1.1")
        @DecimalMax("1.6")
        private BigDecimal d1;

        // Order of annotations doesn't matter
        @DecimalMax("1.6")
        @DecimalMin("1.1")
        @Column(precision = 6, scale = 3)
        private BigDecimal d2;
    }

    @Data
    public static class WithPrecisionScaleAndMinMax {
        // Min/Max should take precedence over precision
        @Column(precision = 6, scale = 4)
        @Min(2)
        @Max(3)
        private BigDecimal d;
    }

    @Data
    public static class WithPrecisionScaleAndNegative {
        @Column(precision = 10, scale = 10)
        @Negative
        private BigDecimal d;
    }

    @Data
    public static class WithPrecisionScaleAndPositive {
        @Column(precision = 10, scale = 10)
        @Positive
        private BigDecimal d;
    }
}
