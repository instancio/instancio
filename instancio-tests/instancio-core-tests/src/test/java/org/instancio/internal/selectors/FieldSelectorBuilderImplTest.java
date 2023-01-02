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
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Pojo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.internal.util.ReflectionUtils.getField;

class FieldSelectorBuilderImplTest {

    private final FieldSelectorBuilderImpl selectorBuilder = new FieldSelectorBuilderImpl();

    @Test
    void named() {
        selectorBuilder.named("name");
        assertThat(build(selectorBuilder)).acceptsAll(Arrays.asList(
                getField(Person.class, "name"),
                getField(Pet.class, "name")));

        assertThat(build(selectorBuilder)).rejects(getField(Address.class, "city"));
    }

    @Test
    void matching() {
        selectorBuilder.matching("^ge.*|.*ge$");
        assertThat(build(selectorBuilder)).acceptsAll(Arrays.asList(
                getField(Person.class, "age"),
                getField(Person.class, "gender")));

        assertThat(build(selectorBuilder)).rejects(getField(Address.class, "city"));
    }

    @Test
    void ofType() {
        selectorBuilder.ofType(String.class);
        assertThat(build(selectorBuilder)).accepts(getField(Person.class, "name"));
        assertThat(build(selectorBuilder)).rejects(getField(Person.class, "age"));
    }

    @Test
    void declaredIn() {
        selectorBuilder.declaredIn(Address.class);
        assertThat(build(selectorBuilder)).accepts(getField(Address.class, "city"));
        assertThat(build(selectorBuilder)).rejects(getField(Person.class, "name"));
    }

    @Test
    void annotated() {
        selectorBuilder.annotated(PersonName.class);
        assertThat(build(selectorBuilder)).accepts(getField(Person.class, "name"));
        assertThat(build(selectorBuilder)).rejects(getField(Pet.class, "name"));
    }

    @Test
    void allCombined() {
        selectorBuilder
                .named("name")
                .ofType(String.class)
                .declaredIn(Person.class)
                .annotated(PersonName.class);

        assertThat(build(selectorBuilder)).accepts(getField(Person.class, "name"));
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> selectorBuilder.named(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Field name must not be null.")
                .hasMessageContaining("Method invocation: fields().named( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.matching(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Regex must not be null.")
                .hasMessageContaining("Method invocation: fields().matching( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.ofType(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Field type must not be null.")
                .hasMessageContaining("Method invocation: fields().ofType( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.declaredIn(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Declaring type must not be null.")
                .hasMessageContaining("Method invocation: fields().declaredIn( -> null <- )");

        assertThatThrownBy(() -> selectorBuilder.annotated(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Field's declared annotation must not be null.")
                .hasMessageContaining("Method invocation: fields().annotated( -> null <- )");
    }

    @Test
    void verifyToString() {
        final String result = selectorBuilder
                .named("name")
                .matching("regex")
                .ofType(String.class)
                .declaredIn(Person.class)
                .annotated(Pojo.class)
                .annotated(PersonName.class)
                .toString();

        assertThat(result).isEqualTo("fields().named(\"name\").matching(\"regex\").ofType(String)" +
                ".declaredIn(Person).annotated(Pojo).annotated(PersonName)");
    }

    private static Predicate<Field> build(FieldSelectorBuilderImpl builder) {
        final PredicateSelector predicateSelector = builder.build();
        return ((PredicateSelectorImpl) predicateSelector).getFieldPredicate();
    }
}
