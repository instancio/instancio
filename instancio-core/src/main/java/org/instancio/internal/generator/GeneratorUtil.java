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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

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
            final Class<?> generatorClass,
            final GeneratorContext context) {

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
}