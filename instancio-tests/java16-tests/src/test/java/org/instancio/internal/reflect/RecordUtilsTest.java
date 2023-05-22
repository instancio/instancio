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
package org.instancio.internal.reflect;

import org.instancio.internal.util.RecordUtils;
import org.instancio.test.support.java16.record.AddressRecord;
import org.instancio.test.support.java16.record.PersonRecord;
import org.instancio.test.support.java16.record.PhoneRecord;
import org.instancio.test.support.pojo.basic.IntegerHolderWithoutDefaultConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecordUtilsTest {

    @Test
    void instantiate() {
        final PhoneRecord result = RecordUtils.instantiate(PhoneRecord.class, "foo", "bar");

        assertThat(result).isNotNull();
        assertThat(result.countryCode()).isEqualTo("foo");
        assertThat(result.number()).isEqualTo("bar");
    }

    @Test
    void instantiateNonRecordClass() {
        final Class<?> nonRecordClass = IntegerHolderWithoutDefaultConstructor.class;
        assertThatThrownBy(() -> RecordUtils.instantiate(nonRecordClass, 123, 345))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class '%s' is not a record!", nonRecordClass.getName());
    }

    @Test
    void getComponentTypes() {
        assertThat(RecordUtils.getComponentTypes(PersonRecord.class))
                .containsExactly(String.class, int.class, AddressRecord.class);

        assertThat(RecordUtils.getComponentTypes(RecordWithoutArgs.class)).isEmpty();
    }

    private record RecordWithoutArgs() {}
}
