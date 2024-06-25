/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.schema;

import org.instancio.documentation.InternalApi;
import org.instancio.settings.Keys;

import java.util.Collection;
import java.util.List;

@InternalApi
public interface DataStore<T> {

    /**
     * Returns all data records with the specified {@code tagValue}.
     *
     * @param tagValue to select data records by
     * @return all records with the specified tag value
     * @see Keys#SCHEMA_TAG_KEY
     * @see Keys#SCHEMA_TAG_VALUE
     */
    T get(String tagValue);

    Collection<String> getPropertyKeys();

    List<String> getAvailableTags();

    /**
     * Returns index of the property, or {@code null} if the data
     * does not contain the property (e.g. for derived properties).
     */
    int indexOf(String propertyName);

    boolean contains(String propertyName);

    int fieldCount();
}