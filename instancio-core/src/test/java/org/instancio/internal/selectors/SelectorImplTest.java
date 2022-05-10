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
import nl.jqno.equalsverifier.Warning;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.ScopeAssert.assertScope;

class SelectorImplTest {

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(SelectorImpl.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    void flatten() {
        final Selector selector = Select.field("foo");
        final List<SelectorImpl> results = ((Flattener) selector).flatten();
        assertThat(results).containsExactly((SelectorImpl) selector);
    }

    @Test
    void toScope() {
        assertScope(Select.field(Foo.class, "fooValue").toScope())
                .hasTargetClass(Foo.class)
                .hasFieldName("fooValue");

        assertScope(Select.all(Foo.class).toScope())
                .hasTargetClass(Foo.class)
                .hasNullField();
    }

    @Test
    void verifyToString() {
        assertThat(Select.field("foo"))
                .hasToString("Selector[(\"foo\")]");

        assertThat(Select.all(Person.class))
                .hasToString("Selector[(Person)]");

        assertThat(Select.field(Person.class, "name"))
                .hasToString("Selector[(Person, \"name\")]");

        assertThat(Select.field(Phone.class, "number").within(Select.scope(Address.class)))
                .hasToString("Selector[(Phone, \"number\"), scope(Address)]");

        assertThat(Select.field(Phone.class, "number").within(
                Select.scope(Person.class, "address"),
                Select.scope(Address.class)))
                .hasToString(
                        "Selector[(Phone, \"number\"), scope(Person, \"address\"), scope(Address)]");
    }
}
