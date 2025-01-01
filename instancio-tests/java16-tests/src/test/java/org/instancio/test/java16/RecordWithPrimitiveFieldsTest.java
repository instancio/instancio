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
package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.PrimitivesRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag({Feature.IGNORE, Feature.WITH_NULLABLE, Feature.SET})
@ExtendWith(InstancioExtension.class)
class RecordWithPrimitiveFieldsTest {

    @Test
    void ignored() {
        final PrimitivesRecord result = Instancio.of(PrimitivesRecord.class)
                .ignore(fields())
                .create();

        assertAllFieldsHaveDefaultValues(result);
    }

    @Test
    void setNull() {
        final PrimitivesRecord result = Instancio.of(PrimitivesRecord.class)
                .set(fields(), null)
                .create();

        assertAllFieldsHaveDefaultValues(result);
    }

    @Test
    void withNullable() {
        final PrimitivesRecord result = Instancio.of(PrimitivesRecord.class)
                .withNullable(fields())
                .create();

        assertThat(result).isNotNull();
    }

    private static void assertAllFieldsHaveDefaultValues(final PrimitivesRecord result) {
        assertThat(result.booleanValue()).isFalse();
        assertThat(result.byteValue()).isZero();
        assertThat(result.shortValue()).isZero();
        assertThat(result.charValue()).isEqualTo('\u0000');
        assertThat(result.intValue()).isZero();
        assertThat(result.longValue()).isZero();
        assertThat(result.floatValue()).isZero();
        assertThat(result.doubleValue()).isZero();
        assertThat(result.objectValue()).isNull();
    }
}