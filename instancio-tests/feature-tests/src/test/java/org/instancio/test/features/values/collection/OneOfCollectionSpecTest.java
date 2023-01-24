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
package org.instancio.test.features.values.collection;

import org.instancio.Gen;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
class OneOfCollectionSpecTest {

    private static final List<String> CHOICES = Arrays.asList("foo", "bar", "baz");

    @Test
    void get() {
        final String result = Gen.oneOf(CHOICES).get();
        assertThat(result).isIn(CHOICES);
    }

    @Test
    void list() {
        final List<String> result = Gen.oneOf(CHOICES).list(100);
        assertThat(result).containsAll(CHOICES);
    }

    @Test
    void map() {
        assertThat(Gen.oneOf(CHOICES).map(String::length)).isEqualTo(3);
    }
}
