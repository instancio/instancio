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
package org.instancio.internal.selectors;

import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.Flattener;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.testsupport.asserts.ScopeAssert;
import org.instancio.testsupport.fixtures.Throwables;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.scope;

class SelectorImplTest {

    @Test
    void root() {
        final SelectorImpl root = SelectorImpl.getRootSelector();
        assertThat(root).hasAllNullFieldsOrPropertiesExcept("scopes", "stackTraceHolder", "depth", "hash");
        assertThat(root.getScopes()).isEmpty();
    }

    @Test
    void isRoot() {
        assertThat(SelectorImpl.getRootSelector().isRoot()).isTrue();
        assertThat(SelectorImpl.builder().build().isRoot()).isFalse();
    }

    @Test
    void getDescription() {
        final Throwable throwable = Throwables.mockThrowable(
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
    void flatten() {
        final Selector selector = Select.field("foo");
        final List<TargetSelector> results = ((Flattener<TargetSelector>) selector).flatten();
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

        assertThat(Select.field(Phone.class, "number").atDepth(3))
                .hasToString("field(Phone, \"number\").atDepth(3)");

        assertThat(Select.field(Phone.class, "number").within(scope(Address.class)))
                .hasToString("field(Phone, \"number\"), scope(Address)");

        assertThat(Select.field(Phone.class, "number").within(
                scope(Person.class, "address"),
                scope(Address.class)))
                .hasToString(
                        "field(Phone, \"number\"), scope(Person, \"address\"), scope(Address)");
    }

    @Test
    void depthValidation() {
        final Selector selector = Select.field(Phone.class, "number");

        assertThatThrownBy(() -> selector.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("depth must not be negative: -1");
    }

    @Nested
    class ToBuilderTest {
        @Test
        void complete() {
            final SelectorImpl selector = SelectorImpl.builder()
                    .targetClass(Person.class)
                    .fieldName("name")
                    .scopes(Collections.singletonList(scope(List.class)))
                    .depth(1)
                    .parent(SelectorImpl.builder().build())
                    .stackTraceHolder(new Throwable())
                    .build();

            final SelectorImpl copy = selector.toBuilder().build();

            assertThat(copy).usingRecursiveComparison().isEqualTo(selector);
        }

        @Test
        void empty() {
            final SelectorImpl selector = SelectorImpl.builder().build();
            final SelectorImpl copy = selector.toBuilder().build();

            assertThat(copy).usingRecursiveComparison().isEqualTo(selector);
        }
    }

    @Nested
    class VerifyEqualsAndHashCode {
        @Test
        void equalsFieldSelector() {
            assertThat(Select.field("name"))
                    .isEqualTo(Select.field("name"))
                    .isNotEqualTo(Select.field("Name"))
                    .isNotEqualTo(Select.field("name").within(scope(Person.class)))
                    .isNotEqualTo(Select.field("name").atDepth(1))
                    .isNotEqualTo(Select.field(Person.class, "name"));
        }

        @Test
        void hashCodeFieldSelector() {
            assertThat(Select.field("name"))
                    .hasSameHashCodeAs(Select.field("name"))
                    .doesNotHaveSameHashCodeAs(Select.field("Name"))
                    .doesNotHaveSameHashCodeAs(Select.field("name").within(scope(Person.class)))
                    .doesNotHaveSameHashCodeAs(Select.field(Person.class, "name"));
        }

        @Test
        void equalsClassSelector() {
            assertThat(Select.all(Person.class))
                    .isEqualTo(Select.all(Person.class))
                    .isNotEqualTo(Select.all(String.class))
                    .isNotEqualTo(Select.all(Person.class).within(scope(List.class)))
                    .isNotEqualTo(Select.field(Person.class, "name"));
        }

        @Test
        void hashCodeClassSelector() {
            assertThat(Select.all(Person.class))
                    .hasSameHashCodeAs(Select.all(Person.class))
                    .doesNotHaveSameHashCodeAs(Select.all(String.class))
                    .doesNotHaveSameHashCodeAs(Select.all(Person.class).within(scope(List.class)))
                    .doesNotHaveSameHashCodeAs(Select.field(Person.class, "name"))
                    .doesNotHaveSameHashCodeAs(Select.root());
        }

        @Test
        void equalsRootSelector() {
            final SelectorImpl anotherRootInstance = SelectorImpl.builder().depth(0).build();

            assertThat(Select.root())
                    .isEqualTo(Select.root())
                    .isEqualTo(anotherRootInstance)
                    .isNotEqualTo(null);
        }

        @Test
        void hashCodeRootSelector() {
            final SelectorImpl anotherRootInstance = SelectorImpl.builder().depth(0).build();

            assertThat(Select.root())
                    .hasSameHashCodeAs(Select.root())
                    .hasSameHashCodeAs(anotherRootInstance);
        }

        @Test
        void equalsWithNull() {
            final Selector selector = Select.field("foo");

            assertThat(selector.equals(null)).isFalse();
        }
    }
}
