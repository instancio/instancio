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
package org.instancio.internal.generator;

import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.ExceptionHandler;
import org.instancio.internal.util.Sonar;
import org.instancio.spi.InstancioSpiException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

@SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
final class GeneratorUtil {

    private GeneratorUtil() {
        // non-instantiable
    }

    /**
     * Instantiates internal generator class using the expected constructor.
     */
    @Nullable
    static Generator<?> instantiateInternalGenerator(
            @NotNull final Class<?> generatorClass,
            @NotNull final GeneratorContext context) {

        try {
            final Constructor<?> constructor = generatorClass.getConstructor(GeneratorContext.class);
            return (Generator<?>) constructor.newInstance(context);
        } catch (final Exception ex) {
            ExceptionHandler.conditionalFailOnError(() -> {
                throw new InstancioException("Error instantiating generator " + generatorClass, ex);
            });
            return null;
        }
    }

    /**
     * Instantiates generator class provided via SPI.
     */
    @NotNull
    static Generator<?> instantiateSpiGenerator(
            @NotNull final Class<?> generatorClass,
            @NotNull final GeneratorContext context) {

        final Constructor<?> constructor = getConstructor(generatorClass);

        if (constructor == null) {
            throw new InstancioSpiException(String.format(
                    "%nGenerator class:%n -> %s%n" +
                            "does not define any of the expected constructors:%n" +
                            " -> constructor with GeneratorContext as the only argument, or%n" +
                            " -> default no-argument constructor", generatorClass.getName()));
        }

        try {
            constructor.setAccessible(true);

            final Object[] args = constructor.getParameterCount() == 1
                    ? new Object[]{context}
                    : new Object[0];

            return (Generator<?>) constructor.newInstance(args);
        } catch (Exception ex) {
            throw new InstancioSpiException("Error instantiating generator " + generatorClass, ex);
        }
    }

    /**
     * Returns constructor with {@link org.instancio.generator.GeneratorContext} parameter, if available,
     * or the default constructor otherwise. If no suitable constructor is found,
     * returns {@code null}.
     */
    @Nullable
    static Constructor<?> getConstructor(@NotNull final Class<?> generatorClass) {
        final Constructor<?>[] constructors = generatorClass.getDeclaredConstructors();
        Constructor<?> defaultCtor = null;

        for (Constructor<?> ctor : constructors) {
            final Parameter[] params = ctor.getParameters();
            if (params.length == 0) {
                defaultCtor = ctor;
            } else if (params.length == 1 && params[0].getType() == GeneratorContext.class) {
                return ctor;
            }
        }
        return defaultCtor;
    }

}