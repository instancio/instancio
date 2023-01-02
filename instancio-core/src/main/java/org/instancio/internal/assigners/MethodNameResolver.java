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
package org.instancio.internal.assigners;

import org.instancio.documentation.InternalApi;

import java.lang.reflect.Field;

/**
 * Resolves methods name for a given field.
 *
 * @since 2.1.0
 */
@InternalApi
public interface MethodNameResolver {

    /**
     * Resolves method name for the specified field.
     *
     * @param field to resolve method fror
     * @return resolved method name, or {@code null} if it could not be resolved
     */
    String resolveFor(Field field);

}
