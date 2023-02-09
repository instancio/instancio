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
package org.instancio.test.util;

import org.instancio.internal.util.LuhnUtils;

import static org.assertj.core.api.Assertions.assertThat;

public final class BVTestUtil {

    public static void assertValidLuhn(
            final int startIdx, final int endIdx, final int checkIdx, final String value) {

        assertThat(LuhnUtils.isLuhnValid(startIdx, endIdx, checkIdx, value))
                .as("Should pass Luhn check: %s", value)
                .isTrue();
    }


    private BVTestUtil() {
        // non-instantiable
    }
}
