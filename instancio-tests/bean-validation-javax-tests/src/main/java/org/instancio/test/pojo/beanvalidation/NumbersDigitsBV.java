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
package org.instancio.test.pojo.beanvalidation;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumbersDigitsBV {

    @Data
    public static class NoFraction {
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
    public static class WithFraction {
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
    public static class WithZeroInteger {
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
