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
package org.instancio.internal.selectors;

import org.instancio.test.support.pojo.misc.getters.BeanStylePojo;
import org.instancio.test.support.pojo.misc.getters.PropertyStylePojo;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.SelectorAssert.assertSelector;

class MethodReferenceHelperTest {

    @Test
    void createSelector() {
        assertSelector(MethodReferenceHelper.createSelector(Person::getAge))
                .hasTargetClass(Person.class)
                .hasFieldName("age")
                .hasNoScope();
    }

    @Test
    void getPropertyName() {
        // java beans style
        assertThat(MethodReferenceHelper.getPropertyName(BeanStylePojo.class, "getFoo")).isEqualTo("foo");
        assertThat(MethodReferenceHelper.getPropertyName(BeanStylePojo.class, "isBar")).isEqualTo("bar");

        // property style getters (e.g. java records)
        assertThat(MethodReferenceHelper.getPropertyName(PropertyStylePojo.class, "foo")).isEqualTo("foo");
        assertThat(MethodReferenceHelper.getPropertyName(PropertyStylePojo.class, "bar")).isEqualTo("bar");
        assertThat(MethodReferenceHelper.getPropertyName(PropertyStylePojo.class, "isBaz")).isEqualTo("isBaz");
        assertThat(MethodReferenceHelper.getPropertyName(PropertyStylePojo.class, "haz")).isEqualTo("haz");
        assertThat(MethodReferenceHelper.getPropertyName(PropertyStylePojo.class, "hazGaz")).isEqualTo("hazGaz");
    }

}
