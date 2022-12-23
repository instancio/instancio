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
package org.instancio.internal.assigners;

import java.lang.reflect.Field;

class SetterMethodNameWithPrefixResolver implements MethodNameResolver {

    private final String prefix;

    SetterMethodNameWithPrefixResolver(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String resolveFor(final Field field) {
        return field.getType() == boolean.class || field.getType() == Boolean.class
                ? getBooleanMethodNameWithPrefix(field)
                : getMethodNameWithPrefix(field.getName());
    }

    private String getMethodNameWithPrefix(final String fieldName) {
        final char[] chars = fieldName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return prefix + new String(chars);
    }

    private String getBooleanMethodNameWithPrefix(final Field field) {
        final String fieldName = field.getName();
        if (fieldName.startsWith("is")) {
            if (fieldName.length() > 2) {
                final char[] chars = fieldName.toCharArray();
                chars[2] = Character.toUpperCase(chars[2]);
                return prefix + new String(chars, 2, chars.length - 2);
            } else {
                // ignore if a method is named "is()"
                return null;
            }
        }
        return getMethodNameWithPrefix(field.getName());
    }
}
