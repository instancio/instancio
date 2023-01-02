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
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Pojo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeSelectorBuilderImplTest {

    private final TypeSelectorBuilderImpl selectorBuilder = new TypeSelectorBuilderImpl();

    @Test
    void of() {
        selectorBuilder.of(CharSequence.class);
        assertThat(build(selectorBuilder)).acceptsAll(Arrays.asList(
                CharSequence.class, String.class, StringBuilder.class));
        assertThat(build(selectorBuilder)).rejects(Integer.class);
    }

    @Test
    void annotated() {
        selectorBuilder.annotated(Pojo.class);
        assertThat(build(selectorBuilder)).accepts(Person.class);
        assertThat(build(selectorBuilder)).rejects(Foo.class);
    }

    @Test
    void allCombined() {
        selectorBuilder
                .of(Object.class)
                .excluding(Person.class)
                .annotated(Pojo.class);

        assertThat(build(selectorBuilder)).acceptsAll(Arrays.asList(Address.class, Phone.class));
        assertThat(build(selectorBuilder)).rejects(Person.class);
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
    }

    @Test
    void verifyToString() {
        final String result = selectorBuilder
                .of(Object.class)
                .excluding(Foo.class)
                .excluding(Bar.class)
                .annotated(Pojo.class)
                .annotated(PersonName.class)
                .toString();

        assertThat(result).isEqualTo("types().of(Object).excluding(Foo).excluding(Bar).annotated(Pojo).annotated(PersonName)");
    }

    private static Predicate<Class<?>> build(TypeSelectorBuilderImpl builder) {
        final PredicateSelector predicateSelector = builder.build();
        return ((PredicateSelectorImpl) predicateSelector).getClassPredicate();
    }
}
