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
package org.instancio.internal.util;

import org.instancio.test.support.pojo.person.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilsTest {

    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @ParameterizedTest
    void isBlankTrue(String value) {
        assertThat(StringUtils.isBlank(value)).isTrue();
    }

    @Test
    void isBlankFalse() {
        assertThat(StringUtils.isBlank("foo")).isFalse();
    }

    @Test
    void isEmpty() {
        assertThat(StringUtils.isEmpty(null)).isTrue();
        assertThat(StringUtils.isEmpty("")).isTrue();
        assertThat(StringUtils.isEmpty(" ")).isFalse();
    }

    @Test
    void trimToEmpty() {
        assertThat(StringUtils.trimToEmpty(null)).isEmpty();
        assertThat(StringUtils.trimToEmpty(" ")).isEmpty();
        assertThat(StringUtils.trimToEmpty(" foo ")).isEqualTo("foo");
    }

    @Test
    void singleQuote() {
        assertThat(StringUtils.singleQuote(null)).isNull();
        assertThat(StringUtils.singleQuote("")).isEqualTo("''");
        assertThat(StringUtils.singleQuote("foo")).isEqualTo("'foo'");
    }

    @Test
    void repeat() {
        assertThat(StringUtils.repeat("a", 0)).isEqualTo("");
        assertThat(StringUtils.repeat("a", 1)).isEqualTo("a");
        assertThat(StringUtils.repeat("a", 3)).isEqualTo("aaa");
        assertThatThrownBy(() -> StringUtils.repeat("a", -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void concatNonNull() {
        assertThat(StringUtils.concatNonNull((String) null)).isEmpty();
        assertThat(StringUtils.concatNonNull(null, null, null)).isEmpty();
        assertThat(StringUtils.concatNonNull((String[]) null)).isEmpty();
        assertThat(StringUtils.concatNonNull(null, "foo", null, "bar", null)).isEqualTo("foobar");
    }

    @Test
    void capitalise() {
        assertThat(StringUtils.capitalise(null)).isNull();
        assertThat(StringUtils.capitalise("")).isEmpty();
        assertThat(StringUtils.capitalise("a")).isEqualTo("A");
        assertThat(StringUtils.capitalise("aa")).isEqualTo("Aa");
        assertThat(StringUtils.capitalise(".")).isEqualTo(".");
    }

    @Test
    void enumToString() {
        assertThat(StringUtils.enumToString(Gender.OTHER)).isEqualTo("Gender.OTHER");
    }

    @Test
    void quoteToString() {
        assertThat(StringUtils.quoteToString(null)).isNull();
        assertThat(StringUtils.quoteToString("")).isEqualTo("\"\"");
        assertThat(StringUtils.quoteToString("123")).isEqualTo("\"123\"");
        assertThat(StringUtils.quoteToString(123)).isEqualTo("123");
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void startsWithAny() {
        assertThat(StringUtils.startsWithAny(null)).isFalse();
        assertThat(StringUtils.startsWithAny("foo", "F", "o")).isFalse();
        assertThat(StringUtils.startsWithAny("foo", "fooo", "FOO")).isFalse();
        assertThat(StringUtils.startsWithAny("foo", "")).isTrue();
        assertThat(StringUtils.startsWithAny("foo", "x", "f", "y")).isTrue();
        assertThat(StringUtils.startsWithAny("foo", "x", "foo")).isTrue();
    }
}
