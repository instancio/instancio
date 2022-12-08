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
package org.instancio.test.support.asserts;

import static org.assertj.core.api.Assertions.assertThat;

public final class Asserts {

    private Asserts() {
        // non-instantiable
    }

    public static void assertAllNulls(final Object... objs) {
        assertThat(objs).containsOnlyNulls();
    }

    public static void assertNoNulls(final Object... objs) {
        assertThat(objs).doesNotContainNull();
    }

    public static void assertNoZeroes(final int... nums) {
        assertThat(nums).doesNotContain(0);
    }

    public static void assertAllZeroes(final int... nums) {
        assertThat(nums).containsOnly(0);
    }
}
