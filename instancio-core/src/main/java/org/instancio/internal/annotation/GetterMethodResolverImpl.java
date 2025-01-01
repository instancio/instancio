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
package org.instancio.internal.annotation;

import org.instancio.internal.nodes.InternalNode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class GetterMethodResolverImpl implements GetterMethodResolver {

    @Override
    public Method getGetter(final InternalNode node) {
        final Field field = node.getField();

        if (field == null) {
            return null;
        }

        final Class<?> declaringClass = field.getDeclaringClass();
        final String fieldName = field.getName();

        Method method = getMethod(declaringClass, getMethodName("get", fieldName));

        if (method != null) {
            return method;
        }

        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            method = getMethod(declaringClass, getMethodName("is", fieldName));

            if (method != null) {
                return method;
            }
        }

        // might be a property-style getter, e.g. "foo" => foo()
        return getMethod(declaringClass, fieldName);
    }

    private static String getMethodName(final String prefix, final String fieldName) {
        final String methodName = prefix + fieldName;
        final char[] ch = methodName.toCharArray();
        final int capitaliseIdx = prefix.length();
        ch[capitaliseIdx] = Character.toUpperCase(ch[capitaliseIdx]);
        return new String(ch);
    }

    private static Method getMethod(final Class<?> declaringClass, final String methodName) {
        try {
            return declaringClass.getDeclaredMethod(methodName);
        } catch (Exception ex) {
            return null;
        }
    }
}
