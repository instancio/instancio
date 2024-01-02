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

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.instancio.test.support.pojo.basic.Numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class RangePositiveBV implements Numbers {
    private static final long MIN_VAL = 10L;
    private static final long MAX_VAL = 20L;

    public static final BigDecimal MIN = BigDecimal.valueOf(MIN_VAL);
    public static final BigDecimal MAX = BigDecimal.valueOf(MAX_VAL);

    //@formatter:off
    @Range(min = MIN_VAL, max = MAX_VAL) private byte primitiveByte;
    @Range(min = MIN_VAL, max = MAX_VAL) private short primitiveShort;
    @Range(min = MIN_VAL, max = MAX_VAL) private int primitiveInt;
    @Range(min = MIN_VAL, max = MAX_VAL) private long primitiveLong;
    @Range(min = MIN_VAL, max = MAX_VAL) private float primitiveFloat;
    @Range(min = MIN_VAL, max = MAX_VAL) private double primitiveDouble;

    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private Byte byteWrapper;
    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private Short shortWrapper;
    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private Integer integerWrapper;
    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private Long longWrapper;
    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private Float floatWrapper;
    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private Double doubleWrapper;

    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private BigInteger bigInteger;
    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private BigDecimal bigDecimal;

    @NotNull @Range(min = MIN_VAL, max = MAX_VAL) private String string;
    //@formatter:on
}
