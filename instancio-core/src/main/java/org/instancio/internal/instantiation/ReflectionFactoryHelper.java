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
package org.instancio.internal.instantiation;

import org.instancio.internal.util.ExceptionUtils;

import java.lang.reflect.Constructor;

/**
 * This class should only be used if {@code sun.reflect.ReflectionFactory}
 * is found on the classpath, otherwise class not found error will be thrown.
 */
@SuppressWarnings("all")
final class ReflectionFactoryHelper {

    // avoid importing sun.reflect.ReflectionFactory to prevent PMD/Checkstyle warnings

    static <T> T createInstance(final Class<T> klass) {
        try {
            final Constructor<T> ctor = getNewConstructorForSerialization(klass);
            return (T) ctor.newInstance();
        } catch (Throwable ex) {
            ExceptionUtils.logException(
                    "Error instantiating {} via newConstructorForSerialization", ex, klass);
            return null;
        }
    }

    private static <T> Constructor<T> getNewConstructorForSerialization(final Class<T> type)
            throws NoSuchMethodException {

        final Constructor<?> constructor = Object.class.getConstructor((Class<?>[]) null);

        return (Constructor<T>) sun.reflect.ReflectionFactory
                .getReflectionFactory()
                .newConstructorForSerialization(type, constructor);
    }

    private ReflectionFactoryHelper() {
        // non-instantiable
    }
}
