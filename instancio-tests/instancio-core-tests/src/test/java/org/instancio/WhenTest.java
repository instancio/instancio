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
package org.instancio;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WhenTest {
    private static final String[] NULL_ELEMENT = {null};

    @Test
    void is() {
        assertThat(When.is("foo"))
                .accepts("foo")
                .rejects(null, "FOO", "bar");

        assertThat(When.is(null)).accepts(NULL_ELEMENT);
    }

    @Test
    void isNot() {
        assertThat(When.isNot("foo"))
                .accepts(null, "FOO", "bar")
                .rejects("foo");

        assertThat(When.isNot(null)).rejects(NULL_ELEMENT);
    }

    @Test
    void isNull() {
        assertThat(When.isNull())
                .accepts(NULL_ELEMENT)
                .rejects("foo");
    }

    @Test
    void isNotNull() {
        assertThat(When.isNotNull())
                .rejects(NULL_ELEMENT)
                .accepts("foo");
    }

    @Test
    void isIn() {
        assertThat(When.isIn("foo", "bar", "baz"))
                .rejects(null, "gaz")
                .accepts("foo", "bar", "baz");
    }
}
