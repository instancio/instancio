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

import org.instancio.Scope;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.testsupport.fixtures.Fixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.internal.util.ReflectionUtils.getField;

class FieldSelectorBuilderImplTest {

    private final FieldSelectorBuilderImpl selectorBuilder = new FieldSelectorBuilderImpl();

    @Test
    void named() {
        selectorBuilder.named("name");
        assertThat(build(selectorBuilder)).acceptsAll(Arrays.asList(
                createNode(Person.class, "name"),
                createNode(Pet.class, "name")));

        assertThat(build(selectorBuilder)).rejects(createNode(Address.class, "city"));
    }

    @Test
    void matching() {
        selectorBuilder.matching("^ge.*|.*ge$");
        assertThat(build(selectorBuilder)).acceptsAll(Arrays.asList(
                createNode(Person.class, "age"),
                createNode(Person.class, "gender")));

        assertThat(build(selectorBuilder)).rejects(createNode(Address.class, "city"));
    }

    @Test
    void ofType() {
        selectorBuilder.ofType(String.class);
        assertThat(build(selectorBuilder)).accepts(createNode(Person.class, "name"));
        assertThat(build(selectorBuilder)).rejects(createNode(Person.class, "age"));
    }

    @Test
    void declaredIn() {
        selectorBuilder.declaredIn(Address.class);
        assertThat(build(selectorBuilder)).accepts(createNode(Address.class, "city"));
        assertThat(build(selectorBuilder)).rejects(createNode(Person.class, "name"));
    }

    @Test
    void annotated() {
        selectorBuilder.annotated(PersonName.class);
        assertThat(build(selectorBuilder)).accepts(createNode(Person.class, "name"));
        assertThat(build(selectorBuilder)).rejects(createNode(Pet.class, "name"));
    }

    @Test
    void allCombined() {
        selectorBuilder
                .named("name")
                .ofType(String.class)
                .declaredIn(Person.class)
                .annotated(PersonName.class);

        assertThat(build(selectorBuilder)).accepts(createNode(Person.class, "name"));
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> selectorBuilder.named(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("field name must not be null.")
                .hasMessageContaining("method invocation: fields().named( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.matching(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("regex must not be null.")
                .hasMessageContaining("method invocation: fields().matching( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.ofType(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("field type must not be null.")
                .hasMessageContaining("method invocation: fields().ofType( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.declaredIn(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("declaring type must not be null.")
                .hasMessageContaining("method invocation: fields().declaredIn( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.annotated(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("field's declared annotation must not be null.")
                .hasMessageContaining("method invocation: fields().annotated( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("depth must not be negative: -1");

        assertThatThrownBy(() -> selectorBuilder.withScopes((Scope[]) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("scopes must not be null.");

        assertThatThrownBy(() -> selectorBuilder.withScopes((Scope) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("scopes vararg must not contain null.");
    }

    @Nested
    class ToStringTest {
        @Test
        void minimal() {
            final String result = selectorBuilder.toString();

            assertThat(result).isEqualTo("fields()");
        }

        @Test
        void predicateDepth() {
            final String result = selectorBuilder
                    .atDepth(d -> true)
                    .toString();

            assertThat(result).isEqualTo("fields().atDepth(Predicate<Integer>)");
        }

        @Test
        void within() {
            final String result = selectorBuilder
                    .atDepth(d -> true)
                    .within(scope(Person.class), fields().ofType(Phone.class).toScope())
                    .toString();

            assertThat(result).isEqualTo("fields().atDepth(Predicate<Integer>)"
                    + ".within(scope(Person), scope(fields().ofType(Phone)))");
        }

        @Test
        void full() {
            final String result = selectorBuilder
                    .named("name")
                    .matching("regex")
                    .ofType(String.class)
                    .declaredIn(Person.class)
                    .annotated(Pojo.class)
                    .annotated(PersonName.class)
                    .atDepth(5)
                    .within(scope(List.class))
                    .toString();

            assertThat(result).isEqualTo("fields().named(\"name\").matching(\"regex\").ofType(String)"
                    + ".declaredIn(Person).annotated(Pojo).annotated(PersonName).atDepth(5)"
                    + ".within(scope(List))");
        }
    }

    // Note: creates a minimal node for tests to pass (will throw an exception if toString() is called)
    private static InternalNode createNode(final Class<?> type, final String field) {
        return InternalNode.builder(type, type, Fixtures.modelContext().getRootType())
                .member(getField(type, field))
                .build();
    }

    private static Predicate<InternalNode> build(FieldSelectorBuilderImpl builder) {
        final PredicateSelectorImpl predicateSelector = builder.build();
        return predicateSelector.getNodePredicate();
    }
}
