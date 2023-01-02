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

import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeclaredFieldsCollectorTest {

    private final FieldCollector fieldsCollector = new DeclaredFieldsCollector();

    @Test
    void getFields() {
        final List<Field> results = fieldsCollector.getFields(Person.class);
        assertThat(results)
                .extracting(Field::getName)
                .containsExactlyInAnyOrder(
                        "finalField",
                        "uuid",
                        "name",
                        "address",
                        "gender",
                        "age",
                        "lastModified",
                        "date",
                        "pets");
    }

}
