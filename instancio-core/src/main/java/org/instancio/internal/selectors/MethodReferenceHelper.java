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
import org.instancio.internal.util.Format;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.instancio.internal.util.Constants.NL;

public final class MethodReferenceHelper {

    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";

    private MethodReferenceHelper() {
        // non-instantiable
    }

    @SuppressWarnings({Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED, "PMD.UseProperClassLoader"})
    public static <T, R> Selector resolve(final GetMethodSelector<T, R> methodRef) {
        try {
            final Class<?> methodRefClass = methodRef.getClass();
            final Method replaceMethod = methodRefClass.getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            final SerializedLambda lambda = (SerializedLambda) replaceMethod.invoke(methodRef);
            final String className = lambda.getImplClass().replace('/', '.');
            final ClassLoader classLoader = ObjectUtils.defaultIfNull(
                    methodRefClass.getClassLoader(), MethodReferenceHelper.class.getClassLoader());

            final Class<?> targetClass = Class.forName(className, true, classLoader);
            final String fieldName = getFieldNameDeclaredInClass(targetClass, lambda.getImplMethodName());

            if (fieldName == null) {
                throw new InstancioApiException(getErrorMessage(lambda, targetClass));
            }

            return Select.field(targetClass, fieldName);
        } catch (NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException
                 | ClassNotFoundException ex) {
            throw new InstancioApiException("Unable to resolve method name from field selector", ex);
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private static String getErrorMessage(final SerializedLambda lambda, final Class<?> targetClass) {
        final String at = Format.firstNonInstancioStackTraceLine(new Throwable());
        return new StringBuilder(1024).append(NL).append(NL)
                .append("Unable to resolve the field from method reference:").append(NL)
                .append("-> ").append(Format.withoutPackage(targetClass)).append("::").append(lambda.getImplMethodName()).append(NL)
                .append("   at ").append(at).append(NL)
                .append(NL)
                .append("Potential causes:").append(NL)
                .append("-> The method and the corresponding field do not follow expected naming conventions").append(NL)
                .append("   See: https://www.instancio.org/user-guide/#method-reference-selector").append(NL)
                .append("-> The method is abstract, declared in a superclass, and the field is declared in a subclass").append(NL)
                .append("-> The method reference is expressed as a lambda function").append(NL)
                .append("   Example:     field((SamplePojo pojo) -> pojo.getValue())").append(NL)
                .append("   Instead of:  field(SamplePojo::getValue)").append(NL)
                .append("-> You are using Kotlin and passing a method reference of a Kotlin class").append(NL)
                .append(NL)
                .append("Possible solutions:").append(NL)
                .append("-> Resolve the above issues, if applicable").append(NL)
                .append("-> Specify the field name explicitly, e.g.").append(NL)
                .append("   field(Example.class, \"someField\")").append(NL)
                .append("-> If using Kotlin, consider creating a 'KSelect' utility class, for example:").append(NL)
                .append(NL)
                .append("   class KSelect {").append(NL)
                .append("       companion object {").append(NL)
                .append("           fun <T, V> field(property: KProperty1<T, V>): TargetSelector {").append(NL)
                .append("               val field = property.javaField!!").append(NL)
                .append("               return Select.field(field.declaringClass, field.name)").append(NL)
                .append("           }").append(NL)
                .append("       }").append(NL)
                .append("   }").append(NL)
                .append(NL)
                .append("   Usage: KSelect.field(SamplePojo::value)").append(NL)
                .toString();
    }

    @Nullable
    static String getFieldNameDeclaredInClass(final Class<?> targetClass, final String methodName) {
        if (hasPrefix(GET_PREFIX, methodName)) {
            final String filedName = getFieldNameByRemovingPrefix(targetClass, methodName, GET_PREFIX.length());
            if (filedName != null) return filedName;
        } else if (hasPrefix(IS_PREFIX, methodName)) {
            final String filedName = getFieldNameByRemovingPrefix(targetClass, methodName, IS_PREFIX.length());
            if (filedName != null) return filedName;
        }
        // class could be using property-style getters (e.g. java record)
        if (ReflectionUtils.isValidField(targetClass, methodName)) {
            return methodName;
        }
        return null;
    }

    @Nullable
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
