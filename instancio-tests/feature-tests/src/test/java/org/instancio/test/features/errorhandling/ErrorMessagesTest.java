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
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.test.support.pojo.generics.container.ItemContainer;
import org.instancio.test.support.pojo.person.Person;
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
        assertThatThrownBy(() -> Instancio.create(() -> null))
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
        assertThatThrownBy(() -> Instancio.of(Person.class).generate(allInts(), null))
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
        assertThatThrownBy(() -> Instancio.of(Person.class).supply(allInts(), (Generator<?>) null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(expectedErrorMsg);

        assertThatThrownBy(() -> Instancio.of(Person.class).supply(allInts(), (Supplier<?>) null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(expectedErrorMsg);
    }

    @Test
    void withTypeParametersGreaterThanRequired() {
        assertThatThrownBy(() -> Instancio.of(Map.class).withTypeParameters(String.class, Integer.class, Long.class))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class 'java.util.Map' has 2 type parameters: [K, V].",
                        "Specify the required type parameters using 'withTypeParameters(Class... types)`");
    }

    @Test
    void withUnnecessaryTypeParameters() {
        assertThatThrownBy(() -> Instancio.of(Person.class).withTypeParameters(Long.class))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class 'org.instancio.test.support.pojo.person.Person' is not generic.",
                        "Specifying type parameters 'withTypeParameters(Long.class)` is not valid for this class.");
    }

    @Test
    void withGenericTypeParameter() {
        assertThatThrownBy(() -> Instancio.of(Map.class).withTypeParameters(String.class, List.class))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "'withTypeParameters(String.class, List.class)` contains a generic class: List<E>'",
                        "For nested generics use the type token API, for example:",
                        "Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});",
                        "or the builder version:",
                        "Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();");
    }

    @Test
    void unboundTypeVariablesErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(ItemContainer.class).create())
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class 'org.instancio.test.support.pojo.generics.container.ItemContainer' has 2 type parameters: [X, Y].",
                        "Specify the required type parameters using 'withTypeParameters(Class... types)`");

        assertThatThrownBy(() -> Instancio.of(List.class).create())
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class 'java.util.List' has 1 type parameters: [E].",
                        "Specify the required type parameters using 'withTypeParameters(Class... types)`");
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