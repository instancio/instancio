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
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class ProtoRepeatedUnsignedTest {

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
            .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
            .set(Keys.LONG_MIN, Long.MIN_VALUE)
            .set(Keys.LONG_MAX, Long.MAX_VALUE);

    @RepeatedTest(Constants.SAMPLE_SIZE_DDD)
    void repeatedUnsignedFields_shouldGenerateNonNegativeValues() {
        final Proto.SupportedNumericTypesAsCollectionValues result =
                Instancio.create(Proto.SupportedNumericTypesAsCollectionValues.class);

        assertThat(result.getUnsignedIntsList()).isNotEmpty().allSatisfy(v -> assertThat(v).isNotNegative());
        assertThat(result.getFixedIntsList()).isNotEmpty().allSatisfy(v -> assertThat(v).isNotNegative());
        assertThat(result.getUnsignedLongsList()).isNotEmpty().allSatisfy(v -> assertThat(v).isNotNegative());
        assertThat(result.getFixedLongsList()).isNotEmpty().allSatisfy(v -> assertThat(v).isNotNegative());
    }
}
