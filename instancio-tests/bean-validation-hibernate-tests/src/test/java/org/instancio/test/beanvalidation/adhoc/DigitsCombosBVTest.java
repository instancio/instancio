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
package org.instancio.test.beanvalidation.adhoc;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class DigitsCombosBVTest {

    @Test
    void digitsAndPositiveOrZero() {
        @Data
        class Pojo {
            @PositiveOrZero
            @Digits(integer = 2, fraction = 5)
            private BigDecimal bd1;

            @Digits(integer = 2, fraction = 5)
            @PositiveOrZero
            private BigDecimal bd2;
        }

        HibernateValidatorUtil.assertValid(Pojo.class, SAMPLE_SIZE_DDD);
    }

    @Test
    void digitsAndNegativeOrZero() {
        @Data
        class Pojo {
            @NegativeOrZero
            @Digits(integer = 2, fraction = 5)
            private BigDecimal bd1;

            @Digits(integer = 2, fraction = 5)
            @NegativeOrZero
            private BigDecimal bd2;
        }

        HibernateValidatorUtil.assertValid(Pojo.class, SAMPLE_SIZE_DDD);
    }

    @Test
    @DisplayName("@Digits, @Max, @Negative")
    void digitsMaxAndNegative() {
        @Data
        class Pojo {
            @Digits(integer = 12, fraction = 0)
            @Max(-10000000000L)
            @Negative
            BigDecimal bd1;

            @Digits(integer = 12, fraction = 0)
            @Negative
            @Max(-10000000000L)
            BigDecimal bd2;

            @Max(-10000000000L)
            @Digits(integer = 12, fraction = 0)
            @Negative
            BigDecimal bd3;

            @Max(-10000000000L)
            @Negative
            @Digits(integer = 12, fraction = 0)
            BigDecimal bd4;

            @Negative
            @Digits(integer = 12, fraction = 0)
            @Max(-10000000000L)
            BigDecimal bd5;

            @Negative
            @Max(-10000000000L)
            @Digits(integer = 12, fraction = 0)
            BigDecimal bd6;
        }

        HibernateValidatorUtil.assertValid(Pojo.class, SAMPLE_SIZE_DDD);
    }

    @Test
    @DisplayName("@Digits, @DecimalMin, @DecimalMax, @Negative")
    void digitsDecimalMinDecimalMaxAndNegative() {
        @Data
        class Pojo {
            @Digits(integer = 3, fraction = 0)
            @DecimalMin("-200")
            @DecimalMax("-100")
            @Negative
            BigDecimal bd1;

            @DecimalMin("-200")
            @DecimalMax("-100")
            @Negative
            @Digits(integer = 3, fraction = 0)
            BigDecimal bd2;
        }

        HibernateValidatorUtil.assertValid(Pojo.class, SAMPLE_SIZE_DDD);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void noFraction(@Given NumbersDigitsBV.NoFraction result) {
        HibernateValidatorUtil.assertValid(result);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withFraction(@Given NumbersDigitsBV.WithFraction result) {
        HibernateValidatorUtil.assertValid(result);
    }

    @Disabled("Validator rejects 0; to fix we'll need to generate a non-zero value")
    @FeatureTag(Feature.UNSUPPORTED)
    @RepeatedTest(SAMPLE_SIZE_DD)
    void withZeroInteger(@Given NumbersDigitsBV.WithZeroInteger result) {
        HibernateValidatorUtil.assertValid(result);
    }


    private static class NumbersDigitsBV {

        @Data
        static class NoFraction {
            //@formatter:off
            @Digits(integer = 1, fraction = 0)
            private byte primitiveByte;
            @Digits(integer = 2, fraction = 0)
            private short primitiveShort;
            @Digits(integer = 3, fraction = 0)
            private int primitiveInt;
            @Digits(integer = 4, fraction = 0)
            private long primitiveLong;
            @Digits(integer = 5, fraction = 0)
            private float primitiveFloat;
            @Digits(integer = 6, fraction = 0)
            private double primitiveDouble;

            @NotNull @Digits(integer = 1, fraction = 0)
            private Byte byteWrapper;
            @NotNull @Digits(integer = 2, fraction = 0)
            private Short shortWrapper;
            @NotNull @Digits(integer = 3, fraction = 0)
            private Integer integerWrapper;
            @NotNull @Digits(integer = 4, fraction = 0)
            private Long longWrapper;
            @NotNull @Digits(integer = 5, fraction = 0)
            private Float floatWrapper;
            @NotNull @Digits(integer = 6, fraction = 0)
            private Double doubleWrapper;

            @NotNull @Digits(integer = 11, fraction = 0)
            private BigInteger bigInteger;
            @NotNull @Digits(integer = 12, fraction = 0)
            private BigDecimal bigDecimal;
            //@formatter:on
        }

        @Data
        static class WithFraction {
            //@formatter:off
            @Digits(integer = 3, fraction = 1)
            private float primitiveFloat;
            @Digits(integer = 4, fraction = 2)
            private double primitiveDouble;

            @NotNull @Digits(integer = 5, fraction = 3)
            private Float floatWrapper;
            @NotNull @Digits(integer = 6, fraction = 4)
            private Double doubleWrapper;

            @NotNull @Digits(integer = 9, fraction = 20)
            private BigDecimal bigDecimal;
            //@formatter:on
        }

        @Data
        static class WithZeroInteger {
            //@formatter:off
            @Digits(integer = 0, fraction = 1)
            private float primitiveFloat;
            @Digits(integer = 0, fraction = 2)
            private double primitiveDouble;

            @NotNull @Digits(integer = 0, fraction = 4)
            private BigDecimal bigDecimal;
            //@formatter:on
        }
    }
}
