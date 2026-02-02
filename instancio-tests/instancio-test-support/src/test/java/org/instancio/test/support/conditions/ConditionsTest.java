/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.support.conditions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionsTest {

    @Test
    void randomString() {
        assertThat("ABC").is(Conditions.RANDOM_STRING);
        assertThat("ABCDEFGHIJ").is(Conditions.RANDOM_STRING);

        assertThat((String) null).isNot(Conditions.RANDOM_STRING);
        assertThat("").isNot(Conditions.RANDOM_STRING);
        assertThat("    ").isNot(Conditions.RANDOM_STRING);
        assertThat("AB").isNot(Conditions.RANDOM_STRING);
        assertThat("ABCDEFGHIJK").isNot(Conditions.RANDOM_STRING);
        assertThat("ABC123").isNot(Conditions.RANDOM_STRING);
        assertThat("abcd").isNot(Conditions.RANDOM_STRING);
    }

    @Test
    void randomInteger() {
        assertThat(1).is(Conditions.RANDOM_INTEGER);
        assertThat(10_000).is(Conditions.RANDOM_INTEGER);

        assertThat((Integer) null).isNot(Conditions.RANDOM_INTEGER);
        assertThat(-1).isNot(Conditions.RANDOM_INTEGER);
        assertThat(0).isNot(Conditions.RANDOM_INTEGER);
        assertThat(10_001).isNot(Conditions.RANDOM_INTEGER);
    }

}
