/*
 * Copyright 2022-2026 the original author or authors.
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

import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.instancio.GetMethodSelector;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.StringUtils;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

final class MethodRef {
    private final Class<?> targetClass;
    private final String methodName;

    private MethodRef(final Class<?> targetClass, final String methodName) {
        this.targetClass = targetClass;
        this.methodName = methodName;
    }

    @SuppressWarnings("PMD.UseProperClassLoader")
    static MethodRef from(final Serializable methodRef) {
        try {
            if (GroovySupport.isGroovyPresent()) {
                final MethodRef groovyMethodRef = GroovySupport.methodRef(methodRef);
                if (groovyMethodRef != null) {
                    return groovyMethodRef;
                }
            }
            final Class<?> methodRefClass = methodRef.getClass();
            final Method replaceMethod = methodRefClass.getDeclaredMethod("writeReplace");
            ReflectionUtils.setAccessible(replaceMethod);
            final SerializedLambda lambda = (SerializedLambda) replaceMethod.invoke(methodRef);
            final ClassLoader classLoader = ObjectUtils.defaultIfNull(
                    methodRefClass.getClassLoader(), MethodRef.class.getClassLoader());

            final Class<?> implClass = loadClass(lambda.getImplClass(), classLoader);

            final MethodRef kotlinMethodRef = KotlinSupport.fromSyntheticAdapter(lambda, implClass, classLoader);
            return kotlinMethodRef != null
                    ? kotlinMethodRef
                    : new MethodRef(implClass, lambda.getImplMethodName());

        } catch (NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException
                 | ClassNotFoundException ex) {

            throw Fail.withUsageError("unable to resolve method name from selector", ex);
        }
    }

    private static Class<?> loadClass(final String internalClassName, final ClassLoader classLoader)
            throws ClassNotFoundException {
        return Class.forName(internalClassName.replace('/', '.'), true, classLoader);
    }

    static String describeGetter(final GetMethodSelector<?, ?> getter) {
        final MethodRef mr = from(getter);
        return mr.getTargetClass().getSimpleName() + "::" + mr.getMethodName();
    }

    Class<?> getTargetClass() {
        return targetClass;
    }

    String getMethodName() {
        return methodName;
    }

    /**
     * Starting with Kotlin 2.4, a method reference SAM-converted to a serializable
     * functional interface (such as {@link GetMethodSelector}) is compiled to a
     * synthetic static adapter in the enclosing class, and the {@link SerializedLambda}
     * points to that adapter instead of the referenced method. The adapter is named
     * {@code <enclosingFunction>$<referencedMethod>}; for example, {@code Phone::getNumber}
     * used inside a function {@code foo()} yields {@code "foo$getNumber"}.
     *
     * <p>The method name is therefore the segment after the last {@code '$'} (the prefix
     * is the enclosing scope, which may itself contain {@code '$'}); the target class is
     * recovered from the lambda's instantiated method type, whose sole parameter is the
     * receiver.
     */
    private static final class KotlinSupport {

        @Nullable
        @SuppressWarnings("unchecked")
        private static final Class<? extends Annotation> KOTLIN_METADATA =
                (Class<? extends Annotation>) ReflectionUtils.loadClass("kotlin.Metadata");

        @Nullable
        static MethodRef fromSyntheticAdapter(
                final SerializedLambda lambda,
                final Class<?> implClass,
                final ClassLoader classLoader) throws ClassNotFoundException {

            if (KOTLIN_METADATA == null
                    || lambda.getImplMethodKind() != MethodHandleInfo.REF_invokeStatic
                    || !implClass.isAnnotationPresent(KOTLIN_METADATA)) {
                return null;
            }

            final String methodName = StringUtils.getSubstringAfterLastChar(
                    lambda.getImplMethodName(), '$');

            if (methodName == null) {
                return null;
            }
            final Class<?> targetClass = resolveTargetClass(lambda.getInstantiatedMethodType(), classLoader);
            return targetClass == null ? null : new MethodRef(targetClass, methodName);
        }

        /**
         * Extracts the target class from the instantiated method type,
         * e.g. {@code "(Lorg/example/Phone;)Ljava/lang/String;"}.
         */
        @Nullable
        private static Class<?> resolveTargetClass(
                final String instantiatedMethodType,
                final ClassLoader classLoader) throws ClassNotFoundException {

            if (!instantiatedMethodType.startsWith("(L")) {
                return null;
            }
            // A descriptor that starts with "(L" always contains the terminating ';'
            // of the receiver type, so indexOf(';') is guaranteed to be >= 2.
            final int endIdx = instantiatedMethodType.indexOf(';');
            return loadClass(instantiatedMethodType.substring(2, endIdx), classLoader);
        }
    }

    private static final class GroovySupport {
        private static final boolean IS_GROOVY_PRESENT =
                ReflectionUtils.loadClass("groovy.lang.Closure") != null;

        static boolean isGroovyPresent() {
            return IS_GROOVY_PRESENT;
        }

        @Nullable
        static MethodRef methodRef(final Object methodRef) {
            if (!Proxy.isProxyClass(methodRef.getClass())) {
                return null;
            }

            final Object invocationHandler = Proxy.getInvocationHandler(methodRef);
            if (invocationHandler instanceof final ConvertedClosure convertedClosure) {
                final Object maybeMethodClosure = convertedClosure.getDelegate();

                if (maybeMethodClosure instanceof final MethodClosure methodClosure) {
                    final Class<?> delegate = (Class<?>) methodClosure.getDelegate();

                    return new MethodRef(delegate, methodClosure.getMethod());
                }
            }

            return null;
        }
    }
}
