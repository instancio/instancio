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

import org.instancio.PredicateSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeContext;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Pojo;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeSelectorBuilderImplTest {

    private final TypeSelectorBuilderImpl selectorBuilder = new TypeSelectorBuilderImpl();

    @Test
    void of() {
        selectorBuilder.of(CharSequence.class);
        assertThat(build(selectorBuilder)).acceptsAll(toNodes(
                CharSequence.class, String.class, StringBuilder.class));

        assertThat(build(selectorBuilder)).rejectsAll(toNodes(Integer.class));
    }

    @Test
    void annotated() {
        selectorBuilder.annotated(Pojo.class);
        assertThat(build(selectorBuilder)).acceptsAll(toNodes(Person.class));
        assertThat(build(selectorBuilder)).rejectsAll(toNodes(Foo.class));
    }

    @Test
    void allCombined() {
        selectorBuilder
                .of(Object.class)
                .excluding(Person.class)
                .annotated(Pojo.class);

        assertThat(build(selectorBuilder)).acceptsAll(toNodes(Address.class, Phone.class));
        assertThat(build(selectorBuilder)).rejectsAll(toNodes(Person.class));
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> selectorBuilder.of(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Type must not be null.")
                .hasMessageContaining("Method invocation: types().of( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.annotated(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Type's declared annotation must not be null.")
                .hasMessageContaining("Method invocation: types().annotated( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.excluding(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Excluded type must not be null.")
                .hasMessageContaining("Method invocation: types().excluding( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Depth must not be negative: -1");
    }

    @Nested
    class ToStringTest {
        @Test
        void minimal() {
            final String result = selectorBuilder.toString();

            assertThat(result).isEqualTo("types()");
        }

        @Test
        void predicateDepth() {
            final String result = selectorBuilder
                    .atDepth(d -> true)
                    .toString();

            assertThat(result).isEqualTo("types().atDepth(Predicate<Integer>)");
        }

        @Test
        void full() {
            final String result = selectorBuilder
                    .of(Object.class)
                    .excluding(Foo.class)
                    .excluding(Bar.class)
                    .annotated(Pojo.class)
                    .annotated(PersonName.class)
                    .atDepth(5)
                    .toString();

            assertThat(result).isEqualTo("types().of(Object).excluding(Foo).excluding(Bar)" +
                    ".annotated(Pojo).annotated(PersonName).atDepth(5)");
        }
    }

    private static List<InternalNode> toNodes(final Class<?>... types) {
        return Arrays.stream(types)
                .map(type -> InternalNode.builder()
                        .type(type)
                        .rawType(type)
                        .targetClass(type)
                        .nodeContext(NodeContext.builder().build())
                        .build())
                .collect(Collectors.toList());
    }

    private static Predicate<InternalNode> build(TypeSelectorBuilderImpl builder) {
        final PredicateSelector predicateSelector = builder.build();
        return ((PredicateSelectorImpl) predicateSelector).getNodePredicate();
    }
}
