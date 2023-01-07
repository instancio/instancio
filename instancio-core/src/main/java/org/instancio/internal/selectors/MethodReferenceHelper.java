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
package org.instancio.internal.selectors;

import org.instancio.GetMethodSelector;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public final class MethodReferenceHelper {

    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";

    private MethodReferenceHelper() {
        // non-instantiable
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    public static <T, R> Selector createSelector(final GetMethodSelector<T, R> methodRef) {
        try {
            final Method replaceMethod = methodRef.getClass().getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            final SerializedLambda lambda = (SerializedLambda) replaceMethod.invoke(methodRef);
            final String className = lambda.getImplClass().replace('/', '.');
            final Class<?> targetClass = Class.forName(className);
            final String fieldName = getPropertyName(targetClass, lambda.getImplMethodName());
            return Select.field(targetClass, fieldName);
        } catch (Exception ex) {
            throw new InstancioApiException("Unable to resolve method name from field selector", ex);
        }
    }

    static String getPropertyName(final Class<?> targetClass, final String methodName) {
        if (hasPrefix(GET_PREFIX, methodName)) {
            final String filedName = getFieldNameByRemovingPrefix(targetClass, methodName, GET_PREFIX.length());
            if (filedName != null) return filedName;
        } else if (hasPrefix(IS_PREFIX, methodName)) {
            final String filedName = getFieldNameByRemovingPrefix(targetClass, methodName, IS_PREFIX.length());
            if (filedName != null) return filedName;
        }
        // class could be using property-style getters (e.g. java record)
        return methodName;
    }

    private static String getFieldNameByRemovingPrefix(
            final Class<?> targetClass,
            final String methodName,
            final int getPrefixLength) {

        final char[] ch = methodName.toCharArray();
        ch[getPrefixLength] = Character.toLowerCase(ch[getPrefixLength]);
        final String filedName = new String(ch, getPrefixLength, ch.length - getPrefixLength);
        return ReflectionUtils.isValidField(targetClass, filedName) ? filedName : null;
    }

    private static boolean hasPrefix(final String prefix, final String methodName) {
        return methodName.startsWith(prefix) && methodName.length() > prefix.length();
    }
}
