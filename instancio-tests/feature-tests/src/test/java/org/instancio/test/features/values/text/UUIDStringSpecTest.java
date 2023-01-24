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
package org.instancio.test.features.values.text;

import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.text;

@FeatureTag(Feature.VALUE_SPEC)
class UUIDStringSpecTest {

    @Test
    void get() {
        assertThat(text().uuid().get()).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<String> results = text().uuid().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final UUID result = text().uuid().map(UUID::fromString);
        assertThat(result).isNotNull();
    }

    @Test
    void words() {
        final String result = text().uuid().upperCase().get();
        assertThat(result).isUpperCase();
    }

    @Test
    void paragraphs() {
        final String result = text().uuid().withoutDashes().get();
        assertThat(result).doesNotContain("-");
    }
}
