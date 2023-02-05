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
package org.instancio.internal;

import org.instancio.documentation.InternalApi;
import org.instancio.generator.GeneratorSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;

/**
 * Processes generator specs prior to generating a value,
 * for example, in order to customise spec parameters.
 *
 * @since 2.7.0
 */
@InternalApi
public interface GeneratorSpecProcessor {

    /**
     * Processes given generator spec.
     *
     * <p>The {@code targetClass} parameter may differ from {@link Field#getType()}.
     * This could be the case where the field is declared using a {@link TypeVariable},
     * in which case field's type will be {@code Object}.
     * Alternatively, a subtype may have been specified via the API.
     *
     * @param spec        generator spec to process
     * @param targetClass class being generated
     * @param field       field the generated value will be assigned to
     * @since 2.7.0
     */
    void process(@NotNull GeneratorSpec<?> spec,
                 @NotNull Class<?> targetClass,
                 @Nullable Field field);
}
