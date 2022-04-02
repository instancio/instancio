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
package org.instancio.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerifyTest {

    private static final String MESSAGE_TEMPLATE = "a message: %s";
    private static final String MESSAGE_ARG = "msg-val";

    @Test
    void notNullThrowsExceptionWithExpectedMessage() {
        assertThatThrownBy(() -> Verify.notNull(null, MESSAGE_TEMPLATE, MESSAGE_ARG))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(String.format(MESSAGE_TEMPLATE, MESSAGE_ARG));
    }

    @Test
    void notNullReturnsTheArgument() {
        final String valueToCheck = "value";
        assertThat(Verify.notNull(valueToCheck, MESSAGE_TEMPLATE, MESSAGE_ARG)).isSameAs(valueToCheck);
    }

    @Test
    void notEmptyThrowsExceptionWhenGivenEmptyArray() {
        assertThatThrownBy(() -> Verify.notEmpty(new String[0], MESSAGE_TEMPLATE, MESSAGE_ARG))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(MESSAGE_TEMPLATE, MESSAGE_ARG));
    }

    @Test
    void notEmptyReturnsTheArrayArgument() {
        final String[] arrayToCheck = new String[]{"test-array"};
        assertThat(Verify.notEmpty(arrayToCheck, MESSAGE_TEMPLATE, MESSAGE_ARG)).isSameAs(arrayToCheck);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void isTrueThrowsExceptionWithExpectedMessageWhenConditionIsFalse() {
        assertThatThrownBy(() -> Verify.isTrue(false, MESSAGE_TEMPLATE, MESSAGE_ARG))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(MESSAGE_TEMPLATE, MESSAGE_ARG));
    }

    @Test
    @SuppressWarnings("java:S2699")
    void isTrueDoesNotThrowExceptionWhenConditionIsTrue() {
        Verify.isTrue(true, MESSAGE_TEMPLATE, MESSAGE_ARG); // no exception thrown
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void isFalseThrowsExceptionWithExpectedMessageWhenConditionIsTrue() {
        assertThatThrownBy(() -> Verify.isFalse(true, MESSAGE_TEMPLATE, MESSAGE_ARG))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(MESSAGE_TEMPLATE, MESSAGE_ARG));
    }

    @Test
    @SuppressWarnings("java:S2699")
    void isFalseDoesNotThrowExceptionWhenConditionIsFalse() {
        Verify.isFalse(false, MESSAGE_TEMPLATE, MESSAGE_ARG); // no exception thrown
    }

    @ParameterizedTest
    @ValueSource(classes = {ArrayList.class, List.class, Map.class, Set.class, SortedSet.class, int[].class, String[].class})
    void isNotArrayCollectionOrMapThrowsExceptionWhenGivenOffendingClass(Class<?> klass) {
        assertThatThrownBy(() -> Verify.isNotArrayCollectionOrMap(klass))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unexpected: " + klass);
    }

    @ParameterizedTest
    @ValueSource(classes = {Object.class, String.class, int.class, Array.class})
    @SuppressWarnings("java:S2699")
    void isNotArrayCollectionOrMapDoesNotThrowException(Class<?> klass) {
        Verify.isNotArrayCollectionOrMap(klass); // no exception thrown
    }
}
