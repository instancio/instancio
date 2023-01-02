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
package org.instancio.internal.reflection;

import org.instancio.test.support.java16.record.AddressRecord;
import org.instancio.test.support.java16.record.PersonRecord;
import org.instancio.test.support.java16.record.PhoneRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecordHelperImplTest {

    private final RecordHelperImpl recordHelper = new RecordHelperImpl();

    @Test
    void isRecord() {
        assertThat(recordHelper.isRecord(PhoneRecord.class)).isTrue();
        assertThat(recordHelper.isRecord(String.class)).isFalse();
    }

    @Test
    void getCanonicalConstructor() {
        assertThat(recordHelper.getCanonicalConstructor(PersonRecord.class))
                .isPresent().get()
                .satisfies(it -> assertThat(it.getParameterTypes()).containsExactly(String.class, int.class, AddressRecord.class));

        assertThatThrownBy(() -> recordHelper.getCanonicalConstructor(String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class 'java.lang.String' is not a record!");
    }
}
