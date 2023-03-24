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

import org.instancio.FieldSelectorBuilder;
import org.instancio.Select;
import org.instancio.TypeSelectorBuilder;
import org.instancio.testsupport.fixtures.Throwables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class PredicateSelectorImplTest {

    @Test
    void getDescription() {
        final Throwable throwable = Throwables.mockThrowable(
                "org.instancio.Foo:1",
                "org.example.ExpectedClass:2",
                "org.instancio.Bar:3");

        final String apiMethod = "anUnusedSelectorMethodName()";

        final PredicateSelectorImpl selector = new PredicateSelectorImpl(
                SelectorTargetKind.CLASS, klass -> false, null, null, apiMethod, throwable);

        assertThat(selector.getDescription()).isEqualTo(
                String.format("anUnusedSelectorMethodName()%n" +
                        "    at org.example.ExpectedClass:2"));
    }

    @Test
    @DisplayName("Field predicate description should be constructed by the builder")
    void fieldPredicateToString() {
        final FieldSelectorBuilder builder = Select.fields().ofType(String.class);
        final PredicateSelectorImpl selector = (PredicateSelectorImpl) ((SelectorBuilder) builder).build();
        assertThat(selector).hasToString("fields().ofType(String)");
    }

    @Test
    @DisplayName("Type predicate description should be constructed by the builder")
    void typePredicateToString() {
        final TypeSelectorBuilder selectorBuilder = Select.types().of(String.class);
        final PredicateSelectorImpl selector = (PredicateSelectorImpl) ((SelectorBuilder) selectorBuilder).build();
        assertThat(selector).hasToString("types().of(String)");
    }

    @Test
    @DisplayName("Field predicate description when using a Predicate<Field> directly (without builder)")
    void fieldPredicateToStringWithoutBuilder() {
        PredicateSelectorImpl selector = (PredicateSelectorImpl) Select.fields(Field::isEnumConstant);
        assertThat(selector).hasToString("fields(Predicate<Field>)");
    }

    @Test
    @DisplayName("Type predicate description when using a Predicate<Class> directly (without builder)")
    void typePredicateToStringWithoutBuilder() {
        PredicateSelectorImpl selector = (PredicateSelectorImpl) Select.types(Class::isArray);
        assertThat(selector).hasToString("types(Predicate<Class>)");
    }


}
