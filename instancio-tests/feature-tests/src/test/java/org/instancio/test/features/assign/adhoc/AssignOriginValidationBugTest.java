/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.assign.adhoc;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;

/**
 * Test for a false-positive ambiguous assignment error thrown by origin
 * validation logic if ignored nodes are not excluded from validation:
 *
 * <pre>
 *  -> The origin selector 'root()' matches multiple values
 *
 *     -> Match 1: Node[Person, depth=0, type=Person]
 *     -> Match 2: Node[IGNORED]
 * </pre>
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignOriginValidationBugTest {

    @Test
    void shouldNotThrowException() {
        final Person result = Instancio.of(Person.class)
                .ignore(field(Person::getUuid))
                .assign(valueOf(Person::getAge).set(123))
                .assign(valueOf(Person::getName).set("foo"))
                .assign(valueOf(Pet::getName).set("bar"))
                .create();

        assertThat(result.getUuid()).isNull();
        assertThat(result.getName()).isEqualTo("foo");
        assertThat(result.getPets()).isNotEmpty()
                .extracting(Pet::getName)
                .containsOnly("bar");
        assertThat(result.getAge()).isEqualTo(123);
    }
}
