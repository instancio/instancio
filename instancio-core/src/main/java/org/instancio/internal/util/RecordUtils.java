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
package org.instancio.internal.util;

/**
 * Helper class for working with {@code java.lang.Record} classes.
 * This class has different implementations depending on Java version.
 */
public final class RecordUtils {

    private RecordUtils() {
        // non-instantiable
    }

    @SuppressWarnings("unused")
    public static <T> T instantiate(final Class<T> recordClass, final Object... args) {
        // no-op implementation until java 16
        return null;
    }

    @SuppressWarnings({"PMD.ReturnEmptyCollectionRatherThanNull", "unused"})
    public static Class<?>[] getComponentTypes(final Class<?> recordClass) {
        // no-op implementation until java 16
        return null; // NOSONAR
    }
}
