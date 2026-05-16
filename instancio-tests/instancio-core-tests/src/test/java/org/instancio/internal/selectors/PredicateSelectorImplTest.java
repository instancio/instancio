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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.GroupableSelector;
import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.TypeSelectorBuilder;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.Flattener;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.testsupport.asserts.ScopeAssert;
import org.instancio.testsupport.fixtures.Throwables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

class PredicateSelectorImplTest {

    @Test
    void root() {
        final PredicateSelectorImpl root = PredicateSelectorImpl.createRootSelector();
        assertThat(root).hasAllNullFieldsOrPropertiesExcept(
                "priority",
                "nodePredicate",
                "selectorDepth",
                "apiInvocationDescription",
                "target",
                "args",
                "scopes",
                "stackTraceHolder",
                "isLenient",
                "isHiddenFromVerboseOutput"
        );

        assertThat(root.getScopes()).isEmpty();
    }

    @Nested
    class VerifyEqualsAndHashCode {

        @Test
        void verifyEqualsAndHashCode() {
            EqualsVerifier.forClass(PredicateSelectorImpl.class)
                    .withIgnoredFields("stackTraceHolder", "nodePredicate")
                    .verify();
        }

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
            final TargetSelector root1 = Select.root();
            final TargetSelector root2 = Select.root();

            assertThat(root1).isEqualTo(root2);
        }

        @Test
        void hashCodeRootSelector() {
            final TargetSelector root1 = Select.root();
            final TargetSelector root2 = Select.root();

            assertThat(root1).hasSameHashCodeAs(root2);
        }

        @Test
        void equalsWithNull() {
            final Selector selector = Select.field("foo");

            assertThat(selector.equals(null)).isFalse();
        }
    }

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
    void flatten() {
        final Selector selector = Select.field("foo");
        final List<TargetSelector> results = ((Flattener<TargetSelector>) selector).flatten();
        assertThat(results).containsExactly(selector);
    }

    @Test
    void toScope() {
        ScopeAssert.assertScope(Select.field(Foo.class, "fooValue").toScope())
                .isPredicateScope();

        ScopeAssert.assertScope(Select.all(Foo.class).toScope())
                .isPredicateScope();
    }

    /**
     * This usage shouldn't be possible using public APIs
     * because {@link GroupableSelector} returned by
     * {@link ScopeableSelector#within(Scope...)} does not
     * have a {@code toScope()} method.
     */
    @Test
    void selectorWithScopesToScope() {
        final PredicateSelectorImpl selector = (PredicateSelectorImpl) Select
                .field(Foo.class, "fooValue")
                .within(scope(Bar.class));

        ScopeAssert.assertScope(selector.toScope())
                .isPredicateScope();
    }

    @Test
    void depthValidation() {
        final Selector selector = Select.field(Phone.class, "number");

        assertThatThrownBy(() -> selector.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("depth must not be negative: -1");

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
        void verifyToString() {
            assertThat(Select.root()).hasToString("root()");

            assertThat(Select.field("foo"))
                    .hasToString("field(\"foo\")");

            assertThat(Select.all(Person.class))
                    .hasToString("all(Person)");

            assertThat(Select.field(Person.class, "name"))
                    .hasToString("field(Person, \"name\")");

            assertThat(Select.field(Person::getName))
                    .hasToString("field(Person::getName)");

            assertThat(Select.field(Phone.class, "number").atDepth(3).lenient())
                    .hasToString("field(Phone, \"number\").atDepth(3).lenient()");

            assertThat(Select.field(Phone.class, "number").within(scope(Address.class)))
                    .hasToString("field(Phone, \"number\").within(scope(Address))");

            assertThat(Select.field(Phone.class, "number").atDepth(3).within(
                    scope(Person.class), scope(Address.class)))
                    .hasToString("field(Phone, \"number\").atDepth(3).within(scope(Person), scope(Address))");

            assertThat(Select.field(Phone.class, "number").within(
                            scope(Person.class, "address"),
                            scope(Address.class))
                    .lenient())
                    .hasToString(
                            "field(Phone, \"number\").within(scope(Person, \"address\"), scope(Address)).lenient()");

            assertThat(Select.setter("setName"))
                    .hasToString("setter(\"setName\")");

            assertThat(Select.setter(Person.class, "setName"))
                    .hasToString("setter(Person, \"setName\")");

            assertThat(Select.setter(Person::setName))
                    .hasToString("setter(Person::setName)");

            assertThat(Select.setter(Person.class, "setName", String.class).lenient())
                    .hasToString("setter(Person, \"setName(String)\").lenient()");

            assertThat(fields().named("foo").atDepth(3))
                    .hasToString("fields().named(\"foo\").atDepth(3)");

            assertThat(types().of(Timestamp.class).atDepth(1))
                    .hasToString("types().of(Timestamp).atDepth(1)");

            assertThat(types().annotated(Pojo.class).annotated(PersonName.class).atDepth(2))
                    .hasToString("types().annotated(Pojo).annotated(PersonName).atDepth(2)");

            assertThat(fields(Field::isEnumConstant))
                    .hasToString("fields(Predicate<Field>)");

            assertThat(types(Class::isArray))
                    .hasToString("types(Predicate<Class>)");

            assertThat(types(t -> t == String.class).atDepth(1).within(scope(StringHolder.class)).lenient())
                    .hasToString("types(Predicate<Class>).atDepth(1).within(scope(StringHolder)).lenient()");

            assertThat(((SelectorBuilder) Select.fields().ofType(String.class).lenient()).build())
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
