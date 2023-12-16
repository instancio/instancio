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

package org.instancio.internal.util;

import org.instancio.test.support.pojo.assignment.OverloadedSettersPojo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SetterMethodComparatorTest {

    private final SetterMethodComparator comparator = new SetterMethodComparator();

    @Test
    void shouldSortByMethodName() throws NoSuchMethodException {
        final Method m1 = Person.class.getMethod("setName", String.class);
        final Method m2 = Person.class.getMethod("setAddress", Address.class);
        final Method m3 = Person.class.getMethod("setGender", Gender.class);
        final Method m4 = Person.class.getMethod("setLastModified", LocalDateTime.class);

        final Method[] methods = {m1, m2, m3, m4};

        Arrays.sort(methods, comparator);

        assertThat(methods)
                .as("should be sorted by method name")
                .containsExactly(m2, m3, m4, m1);
    }

    @Test
    void ifMethodNamesAreTheSame_shouldSortByParameterTypeName() throws NoSuchMethodException {
        final Method m1 = OverloadedSettersPojo.class.getMethod("setTheOne", boolean.class);
        final Method m2 = OverloadedSettersPojo.class.getMethod("setTheOne", CharSequence.class);
        final Method m3 = OverloadedSettersPojo.class.getMethod("setTheOne", Integer.class);
        final Method m4 = OverloadedSettersPojo.class.getMethod("setTheOne", int.class);
        final Method m5 = OverloadedSettersPojo.class.getMethod("setTheOne", Object.class);

        final Method[] methods = {m1, m2, m3, m4, m5};

        Arrays.sort(methods, comparator);

        assertThat(methods)
                .as("should be sorted by parameter type name")
                .containsExactly(m1, m4, m2, m3, m5);
    }
}
