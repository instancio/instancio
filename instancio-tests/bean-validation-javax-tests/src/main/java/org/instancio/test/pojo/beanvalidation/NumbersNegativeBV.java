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
package org.instancio.test.pojo.beanvalidation;

import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.instancio.test.support.pojo.basic.Numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumbersNegativeBV {

    @Data
    public static class NegativeNumbers {
        //@formatter:off
        @Negative private byte primitiveByte;
        @Negative private short primitiveShort;
        @Negative private int primitiveInt;
        @Negative private long primitiveLong;
        @Negative private float primitiveFloat;
        @Negative private double primitiveDouble;

        @NotNull @Negative private Byte byteWrapper;
        @NotNull @Negative private Short shortWrapper;
        @NotNull @Negative private Integer integerWrapper;
        @NotNull @Negative private Long longWrapper;
        @NotNull @Negative private Float floatWrapper;
        @NotNull @Negative private Double doubleWrapper;

        @NotNull @Negative private BigInteger bigInteger;
        @NotNull @Negative private BigDecimal bigDecimal;
        //@formatter:on
    }

    @Data
    public static class NegativeOrZeroNumbers implements Numbers {
        //@formatter:off
        @NegativeOrZero private byte primitiveByte;
        @NegativeOrZero private short primitiveShort;
        @NegativeOrZero private int primitiveInt;
        @NegativeOrZero private long primitiveLong;
        @NegativeOrZero private float primitiveFloat;
        @NegativeOrZero private double primitiveDouble;

        @NotNull @NegativeOrZero private Byte byteWrapper;
        @NotNull @NegativeOrZero private Short shortWrapper;
        @NotNull @NegativeOrZero private Integer integerWrapper;
        @NotNull @NegativeOrZero private Long longWrapper;
        @NotNull @NegativeOrZero private Float floatWrapper;
        @NotNull @NegativeOrZero private Double doubleWrapper;

        @NotNull @NegativeOrZero private BigInteger bigInteger;
        @NotNull @NegativeOrZero private BigDecimal bigDecimal;
        //@formatter:on
    }
}
