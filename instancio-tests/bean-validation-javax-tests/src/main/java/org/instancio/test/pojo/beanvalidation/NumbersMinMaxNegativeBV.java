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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.instancio.test.support.pojo.basic.Numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class NumbersMinMaxNegativeBV implements Numbers {
    private static final long MIN_VAL = -20;
    private static final long MAX_VAL = -10;

    public static final BigDecimal MIN = BigDecimal.valueOf(MIN_VAL);
    public static final BigDecimal MAX = BigDecimal.valueOf(MAX_VAL);

    //@formatter:off
    @Min(MIN_VAL) @Max(MAX_VAL)
    private byte primitiveByte;
    @Min(MIN_VAL) @Max(MAX_VAL)
    private short primitiveShort;
    @Min(MIN_VAL) @Max(MAX_VAL)
    private int primitiveInt;
    @Min(MIN_VAL) @Max(MAX_VAL)
    private long primitiveLong;
    @Min(MIN_VAL) @Max(MAX_VAL)
    private float primitiveFloat;
    @Min(MIN_VAL) @Max(MAX_VAL)
    private double primitiveDouble;

    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private Byte byteWrapper;
    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private Short shortWrapper;
    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private Integer integerWrapper;
    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private Long longWrapper;
    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private Float floatWrapper;
    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private Double doubleWrapper;

    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private BigInteger bigInteger;
    @NotNull @Min(MIN_VAL) @Max(MAX_VAL)
    private BigDecimal bigDecimal;
    //@formatter:on
}
