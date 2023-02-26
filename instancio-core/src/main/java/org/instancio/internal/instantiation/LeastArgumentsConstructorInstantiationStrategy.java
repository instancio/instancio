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
package org.instancio.internal.instantiation;

import org.instancio.internal.util.ExceptionHandler;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.Sonar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Attempts instantiating a class using a non-default constructor with
 * the least number of arguments.
 */
class LeastArgumentsConstructorInstantiationStrategy implements InstantiationStrategy {

    @Override
    @SuppressWarnings({"unchecked", Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED})
    public <T> T createInstance(final Class<T> klass) {
        final Optional<Constructor<?>> optCtor = Arrays.stream(klass.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() > 0)
                .min(Comparator.comparingInt(Constructor::getParameterCount));

        if (!optCtor.isPresent()) {
            return null;
        }

        final Constructor<?> constructor = optCtor.get();
        constructor.setAccessible(true);

        final Parameter[] params = constructor.getParameters();
        final Object[] args = new Object[params.length];

        for (int i = 0; i < args.length; i++) {
            args[i] = ObjectUtils.defaultValue(params[i].getType());
        }

        try {
            return (T) constructor.newInstance(args);
        } catch (Exception ex) {
            ExceptionHandler.logException("Error instantiating {} using {}",
                    ex, klass, getClass().getSimpleName());

            return null;
        }
    }
}
