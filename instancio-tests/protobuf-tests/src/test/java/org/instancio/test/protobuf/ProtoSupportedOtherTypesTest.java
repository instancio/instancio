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

import com.google.protobuf.ByteString;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allBooleans;

@ExtendWith(InstancioExtension.class)
class ProtoSupportedOtherTypesTest {

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void create_shouldGenerateAllFields() {
        final Proto.SupportedOtherTypes result = Instancio.create(Proto.SupportedOtherTypes.class);

        assertThat(result.getBytesField()).isNotEmpty();
        assertThat(result.hasWrappedBool()).isTrue();
        assertThat(result.hasWrappedBytes()).isTrue();
        assertThat(result.getWrappedBytes().getValue()).isNotEmpty();
        assertThat(result.hasDuration()).isTrue();
        assertThat(result.getDuration().getSeconds()).isGreaterThanOrEqualTo(0L);
        assertThat(result.getDuration().getNanos()).isBetween(0, 999_999_999);
        assertThat(result.getTimestamp().getSeconds()).isNotZero();
        assertThat(result.getTimestamp().getNanos()).isBetween(0, 999_999_999);
    }

    @Test
    void setAllBooleans_shouldSetBoolFieldAndWrappedBoolValue() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .set(allBooleans(), true)
                .create();

        assertThat(result.getBoolField()).isTrue();
        assertThat(result.getWrappedBool().getValue()).isTrue();
    }

    @Test
    void setAllByteStrings_shouldSetBytesFieldAndWrappedBytesValue() {
        final ByteString value = ByteString.copyFromUtf8("foo");

        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .set(all(ByteString.class), value)
                .create();

        assertThat(result.getBytesField()).isEqualTo(value);
        assertThat(result.getWrappedBytes().getValue()).isEqualTo(value);
    }
}
