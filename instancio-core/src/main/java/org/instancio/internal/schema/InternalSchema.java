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
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaSpec;

import java.util.Collection;

@InternalApi
public interface InternalSchema {

    /**
     * Returns all the properties defined in the data store.
     * Note that these properties may or may not be explicitly
     * declared by a {@link Schema} subclass.
     */
    Collection<String> getDataProperties();

    <T> SchemaSpec<T> resolveSpec(String propertyName);

    /**
     * Returns a spec for the given invocation of a {@code method}
     * declared by the schema.
     *
     * @param method the schema method that was invoked
     * @param args   method arguments (if any)
     * @param <T>    the type of the spec to return
     * @return a spec with the specified type
     */
    @SuppressWarnings("PMD.UseVarargs")
    <T> SchemaSpec<T> createSpec(SpecMethod method, Object[] args);
}
