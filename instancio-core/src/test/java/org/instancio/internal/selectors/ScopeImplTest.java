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
package org.instancio.internal.selectors;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScopeImplTest {

    @Test
    void constructor() {
        assertThatThrownBy(() -> new ScopeImpl(Person.class, "foo"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Invalid field 'foo'");
    }

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(ScopeImpl.class).verify();
    }

    @Test
    void verifyToString() {
        assertThat(new ScopeImpl(String.class, null)).hasToString("scope(String)");
        assertThat(new ScopeImpl(Person.class, "name")).hasToString("scope(Person, \"name\")");
    }
}
