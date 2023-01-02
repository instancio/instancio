/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal.reflection;

import org.instancio.documentation.InternalApi;

/**
 * A filter for checking whether a {@link Class} should be excluded from processing.
 */
@InternalApi
public interface ClassFilter {

    /**
     * Checks if given class is excluded.
     *
     * @param klass to check
     * @return {@code true} if class should be excluded, {@code false} otherwise
     */
    boolean isExcluded(Class<?> klass);
}
