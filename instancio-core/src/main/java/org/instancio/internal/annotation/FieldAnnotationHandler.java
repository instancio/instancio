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
package org.instancio.internal.annotation;

import org.instancio.documentation.InternalApi;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Handler for a single field annotation.
 *
 * @since 2.7.0
 */
@InternalApi
interface FieldAnnotationHandler {

    /**
     * Processes the specified annotation.
     *
     * @param annotation       annotation to process
     * @param spec             for customising generated values
     * @param targetClass      actual type that will be generated; provided for cases
     *                         where field is declared as a {@link java.lang.reflect.TypeVariable},
     *                         which would result in {@code Object} being returned by {@link Field#getType()}.
     * @param generatorContext the generator context containing current settings
     * @since 2.7.0
     */
    void process(
            Annotation annotation,
            GeneratorSpec<?> spec,
            Class<?> targetClass,
            GeneratorContext generatorContext);
}
