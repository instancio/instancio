/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal.reflection;

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * Helper class for working with {@code java.lang.Record} classes.
 * This interface has different implementations depending on Java version.
 *
 * @since 1.5.0
 */
public interface RecordHelper {

    /**
     * Checks whether given class is a {@code java.lang.Record}.
     *
     * @param klass to check
     * @return {@code true} class is a record, {@code false} otherwise
     * @since 1.5.0
     */
    boolean isRecord(Class<?> klass);

    /**
     * Returns the canonical constructor for the given record class.
     *
     * @param recordClass to return canonical constructor for
     * @return canonical constructor or an empty result if not found
     * @since 1.5.0
     */
    Optional<Constructor<?>> getCanonicalConstructor(Class<?> recordClass);

}
