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
package org.instancio.test.features.values;

import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.string;

@FeatureTag(Feature.VALUE_SPEC)
class StringSpecTest {

    @Test
    void get() {
        assertThat(string().get()).isNotBlank();
    }

    @Test
    void list() {
        final int size = 10;
        final List<String> results = string().length(50).list(size);
        assertThat(new HashSet<>(results)).hasSize(size);
    }

    @Test
    void stream() {
        final int size = 10;
        final Stream<String> result = string().stream().filter(s -> s.contains("X"))
                .limit(size);

        assertThat(result).hasSize(size).allMatch(s -> s.contains("X"));
    }

    @Test
    void map() {
        final BigInteger result = string().digits().map(BigInteger::new);
        assertThat(result).isPositive();
    }

    @Test
    void prefix() {
        assertThat(string().prefix("foo").get()).startsWith("foo");
    }

    @Test
    void length() {
        assertThat(string().length(5).get()).hasSize(5);
        assertThat(string().length(5, 7).get()).hasSizeBetween(5, 7);
        assertThat(string().minLength(10).get()).hasSizeGreaterThanOrEqualTo(10);
        assertThat(string().maxLength(1).get()).hasSizeLessThanOrEqualTo(1);
    }

    @Test
    void cases() {
        assertThat(string().upperCase().get()).isUpperCase();
        assertThat(string().lowerCase().get()).isLowerCase();
        assertThat(string().mixedCase().length(20).get()).isMixedCase();
        assertThat(string().alphaNumeric().length(20).get()).isAlphanumeric();
        assertThat(string().digits().get()).containsOnlyDigits();
    }

    @Test
    void allowEmptyAndNullable() {
        final Stream<String> result = IntStream.range(0, 500)
                .mapToObj(i -> string().allowEmpty().nullable().get());

        assertThat(result).contains("").containsNull();
    }
}
