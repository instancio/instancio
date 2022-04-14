/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.api.errorhandling;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.generics.container.ItemContainer;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.util.SystemProperties;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.field;

@ClearSystemProperty(key = SystemProperties.FAIL_ON_ERROR)
class ErrorMessagesTest {

    private static final Class<InstancioApiException> API_EXCEPTION = InstancioApiException.class;

    @Test
    void nullRootClass() {
        assertThatThrownBy(() -> Instancio.of((Class<?>) null).create())
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
                        "Map<String, Integer> map = Instancio.create(new TypeToken<Map<String, Integer>>(){});",
                        "or the builder version:",
                        "Map<String, Integer> map = Instancio.of(new TypeToken<Map<String, Integer>>(){}).create();");
    }

    @Test
    void nullTypeSuppliedByTypeTokenSupplier() {
        assertThatThrownBy(() -> Instancio.create(() -> null))
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Type token supplier must not return a null Type.",
                        "Provide a valid Type, for example:",
                        "Map<String, Integer> map = Instancio.create(new TypeToken<Map<String, Integer>>(){});",
                        "or the builder version:",
                        "Map<String, Integer> map = Instancio.of(new TypeToken<Map<String, Integer>>(){}).create();");
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
    @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "true")
    void invalidTypeCreatedByGeneratorWithFailOnErrorTrue() {
        assertThatThrownBy(() ->
                Instancio.of(Person.class)
                        .supply(field("name"), () -> 123)
                        .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Could not set value to the field: private java.lang.String org.instancio.test.support.pojo.person.Person.name."
                        + "\nCaused by: Can not set java.lang.String field org.instancio.test.support.pojo.person.Person.name to java.lang.Integer");
    }

    @Test
    void invalidFieldBinding() {
        final String invalidField = "does-not-exist";
        assertThatThrownBy(() ->
                Instancio.of(Person.class)
                        .supply(field(invalidField), () -> null)
                        .create())
                .isInstanceOf(API_EXCEPTION)
                .hasMessage("Invalid field '%s' for class %s", invalidField, Person.class.getName());
    }
}
