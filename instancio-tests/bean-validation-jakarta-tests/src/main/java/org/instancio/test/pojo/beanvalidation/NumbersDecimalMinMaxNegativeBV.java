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

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.instancio.test.support.pojo.basic.Numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Data
public class NumbersDecimalMinMaxNegativeBV implements Numbers {

    public static final String MIN_STR = "-19.5";
    public static final String MAX_STR = "-10.5";
    public static final BigDecimal MIN = new BigDecimal(MIN_STR);
    public static final BigDecimal MAX = new BigDecimal(MAX_STR);
    public static final BigDecimal MIN_ROUNDED = MIN.setScale(0, RoundingMode.HALF_UP);
    public static final BigDecimal MAX_ROUNDED = MAX.setScale(0, RoundingMode.HALF_DOWN);

    //@formatter:off
    @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private byte primitiveByte;
    @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private short primitiveShort;
    @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private int primitiveInt;
    @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private long primitiveLong;
    @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private float primitiveFloat;
    @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private double primitiveDouble;

    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private Byte byteWrapper;
    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private Short shortWrapper;
    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private Integer integerWrapper;
    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private Long longWrapper;
    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private Float floatWrapper;
    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private Double doubleWrapper;

    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private BigInteger bigInteger;
    @NotNull @DecimalMin(MIN_STR) @DecimalMax(MAX_STR)
    private BigDecimal bigDecimal;
    //@formatter:on
}
