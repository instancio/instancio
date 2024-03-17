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
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * Attempts instantiating a class using a non-default constructor with
 * the least number of arguments.
 */
class LeastArgumentsConstructorInstantiationStrategy implements InstantiationStrategy {

    private static final Predicate<Constructor<?>> NON_ZERO_ARG = c -> c.getParameterCount() > 0;
    private static final Predicate<Constructor<?>> NOT_BUILDER = c ->
            !(c.getParameterCount() == 1 && "Builder".equals(c.getParameterTypes()[0].getSimpleName()));

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createInstance(final Class<T> klass) {
        final Constructor<?> ctor = getConstructorWithLeastArgs(klass);

        if (ctor == null) {
            return null;
        }

        final Constructor<?> constructor = ReflectionUtils.setAccessible(ctor);
        final Parameter[] params = constructor.getParameters();
        final Object[] args = new Object[params.length];

        for (int i = 0; i < args.length; i++) {
            args[i] = ObjectUtils.defaultValue(params[i].getType());
        }

        try {
            return (T) constructor.newInstance(args);
        } catch (Exception ex) {
            ExceptionUtils.logException("Error instantiating {} using {}",
                    ex, klass, getClass().getSimpleName());

            return null;
        }
    }

    @Nullable
    private static <T> Constructor<?> getConstructorWithLeastArgs(final Class<T> klass) {
        final Comparator<Constructor<?>> comparator = Comparator.comparingInt(Constructor::getParameterCount);
        final Predicate<Constructor<?>> predicate = NON_ZERO_ARG.and(NOT_BUILDER);
        Constructor<?> result = null;

        for (Constructor<?> ctor : klass.getDeclaredConstructors()) {
            if (predicate.test(ctor) && (result == null || comparator.compare(ctor, result) < 0)) {
                result = ctor;
            }
        }
        return result;
    }
}
