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
package org.instancio.test.features.errorhandling;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.InstancioOfClassApi;
import org.instancio.Selector;
import org.instancio.TypeToken;
import org.instancio.TypeTokenSupplier;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.test.support.pojo.generics.container.ItemContainer;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

class ErrorMessagesTest {

    private static final Class<InstancioApiException> API_EXCEPTION = InstancioApiException.class;

    @Test
    void nullRootClass() {
        assertThatThrownBy(() -> Instancio.of((Class<?>) null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class must not be null.",
                        "Provide a valid class, for example:",
                        "Person person = Instancio.create(Person.class);",
                        "or the builder version:",
                        "Person person = Instancio.of(Person.class).create();");
    }

    @Test
    void nullTypeTokenSupplier() {
        assertThatThrownBy(() -> Instancio.create((TypeToken<?>) null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Type token supplier must not be null.",
                        "Provide a valid type token, for example:",
                        "Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});",
                        "or the builder version:",
                        "Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();");
    }

    @Test
    void nullTypeSuppliedByTypeTokenSupplier() {
        final TypeTokenSupplier<Object> nullSupplier = () -> null;
        assertThatThrownBy(() -> Instancio.create(nullSupplier))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Type token supplier must not return a null Type.",
                        "Provide a valid Type, for example:",
                        "Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});",
                        "or the builder version:",
                        "Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();");
    }

    @Test
    void nullGeneratorPassedToGenerate() {
        final InstancioOfClassApi<Person> api = Instancio.of(Person.class);
        final Selector selector = allInts();
        assertThatThrownBy(() -> api.generate(selector, null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "The second argument of 'generate()' method must not be null.",
                        "To generate a null value, use 'supply(SelectorGroup, () -> null)",
                        "For example:",
                        "\tPerson person = Instancio.of(Person.class)",
                        "\t\t.supply(field(\"firstName\"), () -> null)",
                        "\t\t.create()");
    }

    @Test
    void nullGeneratorPassedToSupply() {
        final CharSequence[] expectedErrorMsg = {
                "The second argument of 'supply()' method must not be null.",
                "To generate a null value, use 'supply(SelectorGroup, () -> null)",
                "For example:",
                "\tPerson person = Instancio.of(Person.class)",
                "\t\t.supply(field(\"firstName\"), () -> null)",
                "\t\t.create()"
        };
        final InstancioOfClassApi<Person> api = Instancio.of(Person.class);
        assertThatThrownBy(() -> api.supply(allInts(), (Generator<?>) null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(expectedErrorMsg);

        assertThatThrownBy(() -> api.supply(allInts(), (Supplier<?>) null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(expectedErrorMsg);
    }

    @Nested
    class WithTypeParametersValidationTest {
        @Test
        void unnecessaryTypeParameters() {
            final InstancioOfClassApi<Person> api = Instancio.of(Person.class);
            assertThatThrownBy(() -> api.withTypeParameters(Long.class))
                    .isExactlyInstanceOf(API_EXCEPTION)
                    .hasMessage(String.format("%nInvalid usage of withTypeParameters() method:%n" +
                            "%n -> Class Person is not generic and does not require type parameters%n"));
        }

        @Test
        void nestedGenerics() {
            final InstancioOfClassApi<?> api = Instancio.of(Map.class);
            assertThatThrownBy(() -> api.withTypeParameters(String.class, List.class))
                    .isExactlyInstanceOf(API_EXCEPTION)
                    .hasMessage(String.format("%n" +
                            "Invalid usage of withTypeParameters() method:%n" +
                            "%n" +
                            "  -> The argument List<E> is a generic class and also requires type parameter(s),%n" +
                            "     but this method does not support nested generics.%n" +
                            "%n" +
                            "To resolve this error:%n" +
                            "%n" +
                            " -> Use a type token, e.g:%n" +
                            "%n" +
                            "    Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});%n" +
                            "%n" +
                            "    or the builder version:%n" +
                            "%n" +
                            "    Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();"));
        }

        @Test
        void oneRequiredZeroProvided() {
            final InstancioOfClassApi<?> api = Instancio.of(List.class);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(API_EXCEPTION)
                    .hasMessage(String.format("%n" +
                            "Invalid usage of withTypeParameters() method:%n" +
                            "%n" +
                            " -> Class java.util.List requires 1 type parameter(s): [E]%n" +
                            " -> The number of parameters provided was 0%n" +
                            "%n" +
                            "To resolve this error:%n" +
                            "%n" +
                            " -> Specify the correct number of parameters, e.g.%n" +
                            "%n" +
                            "    Instancio.of(Map.class).%n" +
                            "        .withTypeParameters(UUID.class, Person.class)%n" +
                            "        .create();%n" +
                            "%n" +
                            " -> Or use a type token:%n" +
                            "%n" +
                            "    Instancio.create(new TypeToken<Map<UUID, Person>>() {});%n" +
                            "%n"));
        }

        @Test
        void twoRequiredZeroProvided() {
            final InstancioOfClassApi<?> api = Instancio.of(ItemContainer.class);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(API_EXCEPTION)
                    .hasMessage(String.format("%n" +
                            "Invalid usage of withTypeParameters() method:%n" +
                            "%n" +
                            " -> Class org.instancio.test.support.pojo.generics.container.ItemContainer requires 2 type parameter(s): [X, Y]%n" +
                            " -> The number of parameters provided was 0%n" +
                            "%n" +
                            "To resolve this error:%n" +
                            "%n" +
                            " -> Specify the correct number of parameters, e.g.%n" +
                            "%n" +
                            "    Instancio.of(Map.class).%n" +
                            "        .withTypeParameters(UUID.class, Person.class)%n" +
                            "        .create();%n" +
                            "%n" +
                            " -> Or use a type token:%n" +
                            "%n" +
                            "    Instancio.create(new TypeToken<Map<UUID, Person>>() {});%n" +
                            "%n"));
        }

        @Test
        void twoRequiredThreeProvided() {
            final InstancioOfClassApi<?> api = Instancio.of(Map.class);

            assertThatThrownBy(() -> api.withTypeParameters(String.class, Integer.class, Long.class))
                    .isExactlyInstanceOf(API_EXCEPTION)
                    .hasMessage(String.format("%n" +
                            "Invalid usage of withTypeParameters() method:%n" +
                            "%n" +
                            " -> Class java.util.Map requires 2 type parameter(s): [K, V]%n" +
                            " -> The number of parameters provided was 3: [String, Integer, Long]%n" +
                            "%n" +
                            "To resolve this error:%n" +
                            "%n" +
                            " -> Specify the correct number of parameters, e.g.%n" +
                            "%n" +
                            "    Instancio.of(Map.class).%n" +
                            "        .withTypeParameters(UUID.class, Person.class)%n" +
                            "        .create();%n" +
                            "%n" +
                            " -> Or use a type token:%n" +
                            "%n" +
                            "    Instancio.create(new TypeToken<Map<UUID, Person>>() {});%n" +
                            "%n"));
        }
    }

    @Test
    void invalidTypeCreatedByGeneratorWithFailOnErrorTrue() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .supply(field("name"), () -> 123);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessage(String.format("%n" +
                        "Throwing exception because:%n" +
                        " -> Keys.ON_SET_FIELD_ERROR = OnSetFieldError.FAIL%n" +
                        "%n" +
                        "Error assigning value to field:%n" +
                        " -> Field: String Person.name%n" +
                        " -> Argument type:  Integer%n" +
                        " -> Argument value: 123%n" +
                        "%n" +
                        "Root cause: %n" +
                        " -> java.lang.IllegalArgumentException: Can not set java.lang.String field org.instancio.test.support.pojo.person.Person.name to java.lang.Integer%n" +
                        "%n" +
                        "To ignore the error and leave the field uninitialised%n" +
                        " -> Update Keys.ON_SET_FIELD_ERROR setting to: OnSetFieldError.IGNORE%n"));
    }

    @Test
    void invalidFieldSelector() {
        final String invalidField = "does-not-exist";

        final InstancioApi<Person> api = Instancio.of(Person.class)
                .supply(field(invalidField), () -> null);

        assertThatThrownBy(api::create)
                .isInstanceOf(API_EXCEPTION)
                .hasMessage("Invalid field '%s' for class %s", invalidField, Person.class.getName());
    }
}