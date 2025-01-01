/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.GetMethodSelector;
import org.instancio.SetMethodSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Serializable;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MethodRefTest {

    private static Stream<Arguments> args() {
        final GetMethodSelector<Person, Integer> getter = Person::getAge;
        final SetMethodSelector<Person, Integer> setter = Person::setAge;

        return Stream.of(
                Arguments.of(getter, "getAge"),
                Arguments.of(setter, "setAge"));
    }

    @MethodSource("args")
    @ParameterizedTest
    void extractMethodRef(final Serializable methodRef, final String expectedMethodName) {
        final MethodRef result = MethodRef.from(methodRef);

        assertThat(result.getTargetClass()).isEqualTo(Person.class);
        assertThat(result.getMethodName()).isEqualTo(expectedMethodName);
    }

    @Test
    void shouldThrowErrorWhenMethodCannotBeResolved() {
        final Serializable notAMethodRef = new Serializable() {};

        assertThatThrownBy(() -> MethodRef.from(notAMethodRef))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("unable to resolve method name from selector");
    }
}
