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
package org.instancio.internal.reflect;

import org.instancio.internal.util.RecordUtils;
import org.instancio.test.support.pojo.record.AddressRecord;
import org.instancio.test.support.pojo.record.PersonRecord;
import org.instancio.test.support.pojo.record.PhoneRecord;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class RecordUtilsTest {

    @Test
    void getCanonicalConstructor() {
        final Constructor<PhoneRecord> ctor = RecordUtils.getCanonicalConstructor(PhoneRecord.class);

        assertThat(ctor.getParameterTypes()).containsExactly(String.class, String.class);
        assertThat(ctor.canAccess(null)).isTrue();
    }

    @Test
    void getCanonicalConstructorOfRecordWithMixedComponentTypes() {
        final Constructor<PersonRecord> ctor = RecordUtils.getCanonicalConstructor(PersonRecord.class);

        assertThat(ctor.getParameterTypes())
                .containsExactly(String.class, int.class, AddressRecord.class);
    }

    @Test
    void getCanonicalConstructorOfRecordWithoutComponents() {
        final Constructor<RecordWithoutArgs> ctor = RecordUtils.getCanonicalConstructor(RecordWithoutArgs.class);

        assertThat(ctor.getParameterTypes()).isEmpty();
    }

    private record RecordWithoutArgs() {}
}
