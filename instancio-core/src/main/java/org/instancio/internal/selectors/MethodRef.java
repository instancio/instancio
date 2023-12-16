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

import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.Sonar;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class MethodRef {
    private final Class<?> targetClass;
    private final String methodName;

    private MethodRef(final Class<?> targetClass, final String methodName) {
        this.targetClass = targetClass;
        this.methodName = methodName;
    }

    @SuppressWarnings({Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED, "PMD.UseProperClassLoader"})
    static MethodRef from(final Serializable methodRef) {
        try {
            final Class<?> methodRefClass = methodRef.getClass();
            final Method replaceMethod = methodRefClass.getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            final SerializedLambda lambda = (SerializedLambda) replaceMethod.invoke(methodRef);
            final String className = lambda.getImplClass().replace('/', '.');
            final ClassLoader classLoader = ObjectUtils.defaultIfNull(
                    methodRefClass.getClassLoader(), MethodRef.class.getClassLoader());

            final Class<?> targetClass = Class.forName(className, true, classLoader);
            final String implMethodName = lambda.getImplMethodName();
            return new MethodRef(targetClass, implMethodName);

        } catch (NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException
                 | ClassNotFoundException ex) {

            throw Fail.withUsageError("unable to resolve method name from selector", ex);
        }
    }

    Class<?> getTargetClass() {
        return targetClass;
    }

    String getMethodName() {
        return methodName;
    }
}
