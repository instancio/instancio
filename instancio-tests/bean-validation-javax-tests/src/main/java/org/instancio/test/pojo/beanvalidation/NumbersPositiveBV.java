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
package org.instancio.test.pojo.beanvalidation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumbersPositiveBV {

    @Data
    public static class PositiveNumbers {
        //@formatter:off
        @Positive private byte primitiveByte;
        @Positive private short primitiveShort;
        @Positive private int primitiveInt;
        @Positive private long primitiveLong;
        @Positive private float primitiveFloat;
        @Positive private double primitiveDouble;

        @NotNull @Positive private Byte byteWrapper;
        @NotNull @Positive private Short shortWrapper;
        @NotNull @Positive private Integer integerWrapper;
        @NotNull @Positive private Long longWrapper;
        @NotNull @Positive private Float floatWrapper;
        @NotNull @Positive private Double doubleWrapper;

        @NotNull @Positive private BigInteger bigInteger;
        @NotNull @Positive private BigDecimal bigDecimal;
        //@formatter:on
    }

    @Data
    public static class PositiveOrZeroNumbers {
        //@formatter:off
        @PositiveOrZero private byte primitiveByte;
        @PositiveOrZero private short primitiveShort;
        @PositiveOrZero private int primitiveInt;
        @PositiveOrZero private long primitiveLong;
        @PositiveOrZero private float primitiveFloat;
        @PositiveOrZero private double primitiveDouble;

        @NotNull @PositiveOrZero private Byte byteWrapper;
        @NotNull @PositiveOrZero private Short shortWrapper;
        @NotNull @PositiveOrZero private Integer integerWrapper;
        @NotNull @PositiveOrZero private Long longWrapper;
        @NotNull @PositiveOrZero private Float floatWrapper;
        @NotNull @PositiveOrZero private Double doubleWrapper;

        @NotNull @PositiveOrZero private BigInteger bigInteger;
        @NotNull @PositiveOrZero private BigDecimal bigDecimal;
        //@formatter:on
    }
}
