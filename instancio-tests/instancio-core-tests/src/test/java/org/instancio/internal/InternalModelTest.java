/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalModelTest {

    @Test
    void verifyToString() {
        assertThat(Instancio.of(Person.class).toModel())
                .hasToString("Model<org.instancio.test.support.pojo.person.Person>");

        assertThat(Instancio.of(new TypeToken<Pair<Long, String>>() {}).toModel())
                .hasToString("Model<org.instancio.test.support.pojo.generics.basic.Pair<java.lang.Long, java.lang.String>>");
    }
}
