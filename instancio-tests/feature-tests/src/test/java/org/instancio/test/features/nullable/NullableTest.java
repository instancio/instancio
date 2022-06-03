/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.nullable;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag(Feature.NULLABLE)
class NullableTest {

    @Test
    void nullableRootObject() {
        final Set<String> results = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            results.add(Instancio.of(String.class)
                    .withNullable(allStrings())
                    .create());
        }
        assertThat(results).containsNull();
    }

    @Test
    void nullableGroup() {
        final List<Person> results = Instancio.of(Person.class)
                .withNullable(all(
                        all(LocalDateTime.class),
                        field(Person.class, "name")))
                .stream()
                .limit(1000L)
                .collect(toList());

        assertThat(results.stream().map(Person::getName).collect(toSet())).containsNull();
        assertThat(results.stream().map(Person::getLastModified).collect(toSet())).containsNull();
    }
}
