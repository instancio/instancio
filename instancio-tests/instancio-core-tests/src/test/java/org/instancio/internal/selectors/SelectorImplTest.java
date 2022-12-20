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
import org.instancio.TargetSelector;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.testsupport.asserts.ScopeAssert;
import org.instancio.testsupport.fixtures.Throwables;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.scope;

class SelectorImplTest {

    @Test
    void root() {
        final SelectorImpl root = SelectorImpl.getRootSelector();
        assertThat(root.getTargetClass().getSimpleName()).isEqualTo("Root");
        assertThat(root.getScopes()).isEmpty();
        assertThat(root.getParent()).isNull();
    }

    @Test
    void getDescription() {
        final Throwable throwable = Throwables.mockThrowable(Throwable.class,
                "org.instancio.Foo:1",
                "org.example.ExpectedClass:2",
                "org.instancio.Bar:3");

        final SelectorImpl selector = SelectorImpl.builder()
                .targetClass(Foo.class)
                .stackTraceHolder(throwable)
                .build();

        assertThat(selector.getDescription()).isEqualTo(
                String.format("all(Foo)%n" +
                        "    at org.example.ExpectedClass:2"));
    }

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(SelectorImpl.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withIgnoredFields("parent", "stackTraceHolder")
                .verify();
    }

    @Test
    void flatten() {
        final Selector selector = Select.field("foo");
        final List<TargetSelector> results = ((Flattener) selector).flatten();
        assertThat(results).containsExactly(selector);
    }

    @Test
    void toScope() {
        ScopeAssert.assertScope(Select.field(Foo.class, "fooValue").toScope())
                .hasTargetClass(Foo.class)
                .hasFieldName("fooValue");

        ScopeAssert.assertScope(Select.all(Foo.class).toScope())
                .hasTargetClass(Foo.class)
                .hasNullField();
    }

    @Test
    void withinReturnsANewSelectorInstance() {
        final SelectorImpl selector = SelectorImpl.builder()
                .targetClass(String.class)
                .build();

        final SelectorImpl scopedSelector = (SelectorImpl) selector.within(scope(StringHolder.class));

        assertThat(selector)
                .as("within() should return a new selector")
                .isNotSameAs(scopedSelector)
                .isNotEqualTo(scopedSelector);

        assertThat(selector.getScopes())
                .as("The original selector should not be modified")
                .isEmpty();

        assertThat(scopedSelector.getScopes()).containsExactly(scope(StringHolder.class));
    }

    @Test
    void verifyToString() {
        assertThat(Select.root()).hasToString("root()");

        assertThat(Select.field("foo"))
                .hasToString("field(\"foo\")");

        assertThat(Select.all(Person.class))
                .hasToString("all(Person)");

        assertThat(Select.field(Person.class, "name"))
                .hasToString("field(Person, \"name\")");

        assertThat(Select.field(Phone.class, "number").within(scope(Address.class)))
                .hasToString("field(Phone, \"number\"), scope(Address)");

        assertThat(Select.field(Phone.class, "number").within(
                scope(Person.class, "address"),
                scope(Address.class)))
                .hasToString(
                        "field(Phone, \"number\"), scope(Person, \"address\"), scope(Address)");
    }
}
