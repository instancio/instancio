/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.PredicateSelector;
import org.instancio.Select;
import org.instancio.TypeSelectorBuilder;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.testsupport.fixtures.Throwables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

class PredicateSelectorImplTest {

    @Test
    void getDescription() {
        final Throwable throwable = Throwables.mockThrowable(
                "org.instancio.Foo:1",
                "org.example.ExpectedClass:2",
                "org.instancio.Bar:3");

        final String apiMethod = "anUnusedSelectorMethodName()";

        final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                .typePredicate(klass -> false)
                .apiInvocationDescription(apiMethod)
                .stackTraceHolder(throwable)
                .build();

        assertThat(selector.getDescription()).isEqualTo(
                String.format("anUnusedSelectorMethodName()%n" +
                        "    at org.example.ExpectedClass:2"));
    }

    @Test
    void depthValidation() {
        final PredicateSelector fieldsSelector = Select.fields(f -> true);
        final PredicateSelector typesSelector = types(t -> true);

        assertThatThrownBy(() -> fieldsSelector.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("depth must not be negative: -1");

        assertThatThrownBy(() -> typesSelector.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("depth must not be negative: -1");
    }

    @Test
    void multipleDepthInvocationsShouldThrowError() {
        final PredicateSelectorImpl.Builder builder = PredicateSelectorImpl.builder()
                .depth(1);

        assertThatThrownBy(() -> builder.depth(2))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessageContaining("depth already set!");
    }

    @Nested
    class PriorityTest {
        @Test
        void fieldPredicatePriority() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder()
                    .fieldPredicate(o -> true)
                    .build();

            assertThat(result.getPriority()).isOne();
        }

        @Test
        void typePredicatePriority() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder()
                    .typePredicate(o -> true)
                    .build();

            assertThat(result.getPriority()).isEqualTo(2);
        }
    }

    @Nested
    class DescriptionTest {
        @Test
        void field() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder()
                    .fieldPredicate(o -> true)
                    .build();

            assertThat(result.getDescription())
                    .startsWith(String.format("fields(Predicate<Field>)%n    at"));
        }

        @Test
        void type() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder()
                    .typePredicate(o -> true)
                    .build();

            assertThat(result.getDescription())
                    .startsWith(String.format("types(Predicate<Class>)%n    at"));
        }

        @Test
        @DisplayName("Custom apiInvocationDescription should not be overwritten by fieldPredicate()")
        void customFieldPredicateDescription() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder()
                    .apiInvocationDescription("foo()")
                    .fieldPredicate(o -> true)
                    .build();

            assertThat(result.getDescription())
                    .startsWith(String.format("foo()%n    at"));
        }

        @Test
        @DisplayName("Custom apiInvocationDescription should not be overwritten by typePredicate()")
        void customTypedPredicateDescription() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder()
                    .apiInvocationDescription("foo()")
                    .typePredicate(o -> true)
                    .build();

            assertThat(result.getDescription())
                    .startsWith(String.format("foo()%n    at"));
        }

        @Test
        void nullDescription() {
            final PredicateSelectorImpl result = PredicateSelectorImpl.builder().build();

            assertThat(result.getDescription()).startsWith(String.format("<selector>%n    at"));
        }
    }

    @Nested
    class ToStringTest {

        @Test
        void fieldsNameWithIntegerDepth() {
            assertThat(fields().named("foo").atDepth(3))
                    .hasToString("fields().named(\"foo\").atDepth(3)");
        }

        @Test
        void typesOfWithIntegerDepth() {
            assertThat(types().of(Timestamp.class).atDepth(1))
                    .hasToString("types().of(Timestamp).atDepth(1)");
        }

        @Test
        void typesAnnotatedAtDepth() {
            assertThat(types().annotated(Pojo.class).annotated(PersonName.class).atDepth(2))
                    .hasToString("types().annotated(Pojo).annotated(PersonName).atDepth(2)");
        }

        @Test
        void fieldPredicate() {
            assertThat(fields(Field::isEnumConstant))
                    .hasToString("fields(Predicate<Field>)");
        }

        @Test
        void typePredicate() {
            assertThat(types(Class::isArray))
                    .hasToString("types(Predicate<Class>)");
        }

        @Test
        void predicateAtDepthWithinLenient() {
            assertThat(types(t -> t == String.class).atDepth(1).within(scope(StringHolder.class)).lenient())
                    .hasToString("types(Predicate<Class>).atDepth(1).within(scope(StringHolder)).lenient()");
        }

        @Test
        void fieldSelectorBuilderLenient() {
            final FieldSelectorBuilder builder = (FieldSelectorBuilder) Select.fields().ofType(String.class).lenient();

            assertThat(((SelectorBuilder) builder).build())
                    .hasToString("fields().ofType(String).lenient()");
        }

        @Test
        void typeSelectorBuilderLenient() {
            final TypeSelectorBuilder selectorBuilder = (TypeSelectorBuilder) types().of(String.class).lenient();

            assertThat(((SelectorBuilder) selectorBuilder).build())
                    .hasToString("types().of(String).lenient()");
        }

        @Test
        void fieldPredicateWithIntegerDepth() {
            final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                    .fieldPredicate(o -> true)
                    .depth(3)
                    .build();

            assertThat(selector).hasToString("fields(Predicate<Field>).atDepth(3)");
        }

        @Test
        void fieldPredicateWithPredicateDepth() {
            final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                    .fieldPredicate(o -> true)
                    .depth(d -> true)
                    .build();

            assertThat(selector).hasToString("fields(Predicate<Field>).atDepth(Predicate<Integer>)");
        }

        @Test
        void typePredicateWithIntegerDepth() {
            final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                    .typePredicate(o -> true)
                    .depth(3)
                    .build();

            assertThat(selector).hasToString("types(Predicate<Class>).atDepth(3)");
        }

        @Test
        void typePredicateWithPredicateDepthLnient() {
            final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                    .typePredicate(o -> true)
                    .depth(d -> true)
                    .lenient()
                    .build();

            assertThat(selector).hasToString(
                    "types(Predicate<Class>).atDepth(Predicate<Integer>).lenient()");
        }

        @Test
        void withinSingleScope() {
            final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                    .typePredicate(o -> true)
                    .scopes(Collections.singletonList(scope(Phone.class)))
                    .build();

            assertThat(selector).hasToString(
                    "types(Predicate<Class>).within(scope(Phone))");
        }

        @Test
        void withinMultipleScopes() {
            final PredicateSelectorImpl selector = PredicateSelectorImpl.builder()
                    .typePredicate(o -> true)
                    .scopes(Arrays.asList(
                            scope(Person.class),
                            scope(Phone.class)))
                    .build();

            assertThat(selector).hasToString(
                    "types(Predicate<Class>).within(scope(Person), scope(Phone))");
        }
    }
}
