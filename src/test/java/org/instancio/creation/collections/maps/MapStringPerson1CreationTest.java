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
package org.instancio.creation.collections.maps;

import org.instancio.pojo.person.Person;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapStringPerson1CreationTest extends CreationTestTemplate<Map<String, Person>> {

    @Override
    protected void verify(Map<String, Person> result) {
        assertThat(result).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        assertThat(result.entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(String.class);
                    assertThat(entry.getValue()).isInstanceOf(Person.class)
                            .satisfies(person -> {
                                assertThat(person.getName()).isNotBlank();
                                assertThat(person.getAddress()).isNotNull();
                            });
                });

    }

}
