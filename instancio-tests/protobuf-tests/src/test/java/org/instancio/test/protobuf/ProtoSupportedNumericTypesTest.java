/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.protobuf;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(InstancioExtension.class)
class ProtoSupportedNumericTypesTest {

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.BYTE_MIN, Byte.MIN_VALUE)
            .set(Keys.BYTE_MAX, Byte.MAX_VALUE)
            .set(Keys.SHORT_MIN, Short.MIN_VALUE)
            .set(Keys.SHORT_MAX, Short.MAX_VALUE)
            .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
            .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
            .set(Keys.LONG_MIN, Long.MIN_VALUE)
            .set(Keys.LONG_MAX, Long.MAX_VALUE)
            .set(Keys.FLOAT_MIN, -Float.MAX_VALUE)
            .set(Keys.FLOAT_MAX, Float.MAX_VALUE)
            .set(Keys.DOUBLE_MIN, -Double.MAX_VALUE)
            .set(Keys.DOUBLE_MAX, Double.MAX_VALUE);

    /**
     * Protobuf numeric type ranges provide the hard limits for generated values.
     * There we should generate non-negative integers even if the {@link Settings}
     * are configured with {@code [Integer.MIN_VALUE..Integer.MAX_VALUE]}.
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_DDD)
    void numericTypeRanges_shouldHonourProtobufRanges() {
        final Proto.SupportedNumericTypes result = Instancio.create(Proto.SupportedNumericTypes.class);

        // signed int
        assertThat(result.getStandardInt32()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertThat(result.getSignedInt32()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertThat(result.getSignedFixed32()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
        // signed long
        assertThat(result.getStandardInt64()).isBetween(Long.MIN_VALUE, Long.MAX_VALUE);
        assertThat(result.getSignedInt64()).isBetween(Long.MIN_VALUE, Long.MAX_VALUE);
        assertThat(result.getSignedFixed64()).isBetween(Long.MIN_VALUE, Long.MAX_VALUE);
        // unsigned int
        assertThat(result.getUnsignedInt32()).isBetween(0, Integer.MAX_VALUE);
        assertThat(result.getFixedInt32()).isBetween(0, Integer.MAX_VALUE);
        // unsigned long
        assertThat(result.getUnsignedInt64()).isBetween(0L, Long.MAX_VALUE);
        assertThat(result.getFixedInt64()).isBetween(0L, Long.MAX_VALUE);
        // floating point
        assertThat(result.getSinglePrecision()).isBetween(-Float.MAX_VALUE, Float.MAX_VALUE);
        assertThat(result.getDoublePrecision()).isBetween(-Double.MAX_VALUE, Double.MAX_VALUE);
        // wrappers
        assertThat(result.hasWrappedDouble()).isTrue();
        assertThat(result.getWrappedDouble().getValue()).isBetween(-Double.MAX_VALUE, Double.MAX_VALUE);
        assertThat(result.hasWrappedFloat()).isTrue();
        assertThat(result.getWrappedFloat().getValue()).isBetween(-Float.MAX_VALUE, Float.MAX_VALUE);
        assertThat(result.hasWrappedInt32()).isTrue();
        assertThat(result.getWrappedInt32().getValue()).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertThat(result.hasWrappedInt64()).isTrue();
        assertThat(result.getWrappedInt64().getValue()).isBetween(Long.MIN_VALUE, Long.MAX_VALUE);
        assertThat(result.hasWrappedUint32()).isTrue();
        assertThat(result.getWrappedUint32().getValue()).isBetween(0, Integer.MAX_VALUE);
        assertThat(result.hasWrappedUint64()).isTrue();
        assertThat(result.getWrappedUint64().getValue()).isBetween(0L, Long.MAX_VALUE);
    }

    @Test
    void settingsCanNarrowDownGeneratedRanges() {
        final Proto.SupportedNumericTypes result = Instancio.of(Proto.SupportedNumericTypes.class)
                .withSetting(Keys.INTEGER_MIN, 10)
                .withSetting(Keys.INTEGER_MAX, 10)
                .withSetting(Keys.LONG_MIN, 20L)
                .withSetting(Keys.LONG_MAX, 20L)
                .create();

        assertThat(result.getStandardInt32())
                .isEqualTo(result.getUnsignedInt32())
                .isEqualTo(result.getSignedInt32())
                .isEqualTo(result.getFixedInt32())
                .isEqualTo(result.getSignedFixed32())
                .isEqualTo(10);

        assertThat(result.getStandardInt64())
                .isEqualTo(result.getUnsignedInt64())
                .isEqualTo(result.getSignedInt64())
                .isEqualTo(result.getFixedInt64())
                .isEqualTo(result.getSignedFixed64())
                .isEqualTo(20L);

        // Wrapper types honour the same Settings as their underlying primitive type
        assertThat(result.getWrappedInt32().getValue()).isEqualTo(10);
        assertThat(result.getWrappedUint32().getValue()).isEqualTo(10);
        assertThat(result.getWrappedInt64().getValue()).isEqualTo(20L);
        assertThat(result.getWrappedUint64().getValue()).isEqualTo(20L);
    }

    @Test
    void negativeIntegerMaxWithUnsignedField_shouldThrowError() {
        final InstancioApi<Proto.SupportedNumericTypes> api = Instancio.of(Proto.SupportedNumericTypes.class)
                .withSetting(Keys.INTEGER_MAX, -10);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("""
                        
                        
                        Error creating an object
                         -> at org.assertj.core.api.ThrowableAssert.catchThrowable(ThrowableAssert.java:66)
                        
                        Reason: invalid Settings configuration for an unsigned Protobuf type
                        
                         -> Protobuf type: uint32, fixed32 (non-negative values only)
                        
                        This error was thrown because:
                        
                         -> Protobuf unsigned types store only non-negative values
                         -> Keys.INTEGER_MAX ('integer.max') is set to -10, therefore no valid value can be generated
                        
                        To resolve this error:
                        
                         -> Set Keys.INTEGER_MAX to a non-negative value (>= 0)
                         -> Or remove the Keys.INTEGER_MAX override to use the Instancio default
                        
                        
                        """);
    }

    /**
     * Uses same error message template as {@link #negativeIntegerMaxWithUnsignedField_shouldThrowError()}.
     */
    @Test
    void negativeLongMaxWithUnsignedField_shouldThrowError() {
        final InstancioApi<Proto.SupportedNumericTypes> api = Instancio.of(Proto.SupportedNumericTypes.class)
                .withSetting(Keys.LONG_MAX, -10L);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Keys.LONG_MAX")
                .hasMessageContaining("-10")
                .hasMessageContaining("uint64, fixed64");
    }

}
