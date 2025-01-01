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
package org.instancio.internal.generator;

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.Fail;

import java.lang.reflect.Constructor;

final class GeneratorUtil {

    private GeneratorUtil() {
        // non-instantiable
    }

    /**
     * Instantiates internal generator class using the expected constructor.
     */
    @SuppressWarnings("unchecked")
    static <T> Generator<T> instantiateInternalGenerator(
            final Class<?> generatorClass,
            final GeneratorContext context) {

        try {
            final Constructor<?> constructor = generatorClass.getConstructor(GeneratorContext.class);
            return (Generator<T>) constructor.newInstance(context);
        } catch (Exception ex) {
            throw Fail.withFataInternalError("Error instantiating generator %s", generatorClass, ex);
        }
    }
}