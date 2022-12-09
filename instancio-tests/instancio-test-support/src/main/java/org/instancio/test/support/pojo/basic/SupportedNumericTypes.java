/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.support.pojo.basic;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Getter
public class SupportedNumericTypes {
    private byte primitiveByte;
    private short primitiveShort;
    private int primitiveInt;
    private long primitiveLong;
    private float primitiveFloat;
    private double primitiveDouble;

    private Byte byteWrapper;
    private Short shortWrapper;
    private Integer integerWrapper;
    private Long longWrapper;
    private Float floatWrapper;
    private Double doubleWrapper;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SupportedNumericTypes that = (SupportedNumericTypes) o;
        return getPrimitiveByte() == that.getPrimitiveByte()
                && getPrimitiveShort() == that.getPrimitiveShort()
                && getPrimitiveInt() == that.getPrimitiveInt()
                && getPrimitiveLong() == that.getPrimitiveLong()
                && Float.compare(that.getPrimitiveFloat(), getPrimitiveFloat()) == 0
                && Double.compare(that.getPrimitiveDouble(), getPrimitiveDouble()) == 0
                && Objects.equals(getByteWrapper(), that.getByteWrapper())
                && Objects.equals(getShortWrapper(), that.getShortWrapper())
                && Objects.equals(getIntegerWrapper(), that.getIntegerWrapper())
                && Objects.equals(getLongWrapper(), that.getLongWrapper())
                && Objects.equals(getFloatWrapper(), that.getFloatWrapper())
                && Objects.equals(getDoubleWrapper(), that.getDoubleWrapper());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrimitiveByte(), getPrimitiveShort(),
                getPrimitiveInt(), getPrimitiveLong(), getPrimitiveFloat(),
                getPrimitiveDouble(), getByteWrapper(), getShortWrapper(),
                getIntegerWrapper(), getLongWrapper(), getFloatWrapper(),
                getDoubleWrapper());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
