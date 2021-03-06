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

import javax.annotation.Nullable;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isBlank(@Nullable final String s) {
        return s == null || "".equals(s.trim());
    }

    public static String trimToEmpty(@Nullable final String s) {
        return s == null ? "" : s.trim();
    }

    public static String repeat(final String s, final int times) {
        if (times < 0) throw new IllegalArgumentException("Number of times must be positive: " + times);
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static boolean startsWithAny(@Nullable final String s, final String... prefixes) {
        if (s == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (s.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
