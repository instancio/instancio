/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.gradle;

import org.instancio.Instancio;
import org.instancio.InstancioMetaModel;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@InstancioMetaModel(classes = {
        ClassFromSameModule.class,
        Person.class
})
class MetaModelWithGradleTest {

    private static final String EXPECTED = "foo";

    @Test
    void classFromSameModule() {
        final String expected = EXPECTED;
        final ClassFromSameModule result = Instancio.of(ClassFromSameModule.class)
                .supply(ClassFromSameModule_.value, () -> expected)
                .create();

        assertEquals(expected, result.getValue());
    }

    @Test
    void classFromAnotherModule() {
        final Person result = Instancio.of(Person.class)
                .supply(Person_.name, () -> EXPECTED)
                .create();

        assertEquals(EXPECTED, result.getName());
    }

}
