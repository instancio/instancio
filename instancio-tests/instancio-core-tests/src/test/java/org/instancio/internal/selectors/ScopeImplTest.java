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
package org.instancio.internal.selectors;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.Select;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScopeImplTest {

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(ScopeImpl.class)
                .withNonnullFields("target")
                .verify();
    }

    @Test
    void verifyToString() {
        assertThat(new ScopeImpl(new TargetClass(String.class), null))
                .hasToString("scope(String)");

        assertThat(Select.field("name").toScope())
                .hasToString("scope(\"name\")");

        assertThat(new ScopeImpl(new TargetFieldName(Person.class, "name"), null))
                .hasToString("scope(Person, \"name\")");

        assertThat(Select.setter(Person.class, "setName").toScope())
                .hasToString("scope(Person, setName)");

        assertThat(Select.setter(Person.class, "setName", String.class).toScope())
                .hasToString("scope(Person, setName(String))");

        // with depth
        assertThat(new ScopeImpl(new TargetClass(String.class), 1))
                .hasToString("scope(String, atDepth(1))");

        assertThat(new ScopeImpl(new TargetFieldName(Person.class, "name"), 2))
                .hasToString("scope(Person, \"name\", atDepth(2))");

        assertThat(Select.setter(Person.class, "setName", String.class).atDepth(3).toScope())
                .hasToString("scope(Person, setName(String), atDepth(3))");

        // from predicate
        assertThat(Select.fields(f -> true).atDepth(1).toScope())
                .hasToString("scope(fields(Predicate<Field>).atDepth(1))");

        assertThat(Select.types(t -> true).atDepth(1).toScope())
                .hasToString("scope(types(Predicate<Class>).atDepth(1))");

        // from predicate builder
        assertThat(Select.fields().ofType(String.class).toScope())
                .hasToString("scope(fields().ofType(String))");

        assertThat(Select.types().of(String.class).toScope())
                .hasToString("scope(types().of(String))");
    }
}
