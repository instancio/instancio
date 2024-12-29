/*
 * Copyright 2022-2024 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.instancio.internal.util.ErrorMessageUtils.invalidStringTemplate;

public final class StringUtils {

    public static boolean isEmpty(@Nullable final String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(@Nullable final String s) {
        if (isEmpty(s)) {
            return true;
        }
        for (int i = 0, length = s.length(); i < length; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public static String trimToEmpty(@Nullable final String s) {
        return s == null ? "" : s.trim();
    }

    @Nullable
    public static String trimToNull(@Nullable final String s) {
        if (s == null) {
            return null;
        }
        final String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
        if (s == null || s.isEmpty()) {
            return s;
        }
        final String first = s.substring(0, 1).toUpperCase(Locale.getDefault());
        return s.length() == 1 ? first : first + s.substring(1);
    }

    public static <E extends Enum<E>> String enumToString(E val) {
        return val.getClass().getSimpleName() + "." + val.name();
    }

    /**
     * Wraps the given {@code value} in quotes if it's a String,
     * otherwise returns a {@code toString()} representation.
     */
    public static String quoteStringValue(final Object value) {
        if (value == null) return null;
        if (value instanceof String) {
            return '"' + value.toString() + '"';
        }
        return value.toString();
    }

    /**
     * Extracts properties keys from a string template, e.g. given
     * the string {@code "${foo} and ${bar}"} returns {@code ["foo", "bar"]}.
     */
    public static List<String> getTemplateKeys(final String template) {
        final List<String> results = new ArrayList<>();

        final int length = template.length();
        int i = 0;

        while (i < length) {
            if (template.charAt(i) == '$' && i + 1 < length && template.charAt(i + 1) == '{') {
                final int j = template.indexOf('}', i + 2);

                if (j == -1) {
                    throw Fail.withUsageError(invalidStringTemplate(
                            template, "unterminated template key"));
                }
                final String key = template.substring(i + 2, j);
                if (key.isEmpty() || key.contains("${")) {
                    final String reason = String.format("invalid template key '${%s}'", key);
                    throw Fail.withUsageError(invalidStringTemplate(template, reason));
                }
                results.add(key);
                i = j + 1;
            } else {
                i++;
            }
        }
        return results;
    }

    private StringUtils() {
        // non-instantiable
    }
}
