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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class StringUtils {

    public static boolean isEmpty(@Nullable final String s) {
        return s == null || "".equals(s);
    }

    public static boolean isBlank(@Nullable final String s) {
        return s == null || "".equals(s.trim());
    }

    @NotNull
    public static String trimToEmpty(@Nullable final String s) {
        return s == null ? "" : s.trim();
    }

    @Nullable
    public static String singleQuote(@Nullable final String s) {
        return s == null ? null : "'" + s + "'";
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
        if (s == null || prefixes.length == 0) {
            return false;
        }
        for (String prefix : prefixes) {
            if (s.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static String concatNonNull(final String... values) {
        final StringBuilder sb = new StringBuilder();
        if (values != null) {
            for (String val : values) {
                if (val != null) {
                    sb.append(val);
                }
            }
        }
        return sb.toString();
    }

    public static String capitalise(@Nullable final String s) {
        if (s == null || s.equals("")) {
            return s;
        }
        final String first = s.substring(0, 1).toUpperCase(Locale.getDefault());
        return s.length() == 1 ? first : first + s.substring(1);
    }

    public static <E extends Enum<E>> String enumToString(E val) {
        return val.getClass().getSimpleName() + "." + val.name();
    }

    public static String quoteToString(final Object value) {
        if (value == null) return null;
        final String str = value.toString();
        if (value instanceof String) {
            return '"' + str + '"';
        }
        return str;
    }

    private StringUtils() {
        // non-instantiable
    }
}
