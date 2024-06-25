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

import org.instancio.schema.Schema;
import org.instancio.schema.SchemaSpec;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappableSpecsResolver {

    public Map<String, SchemaSpec<?>> getMappableSpecs(final Schema schema) {
        final InternalSchema internalSchema = (InternalSchema) schema;
        final List<String> dataProperties = new ArrayList<>(internalSchema.getDataProperties());
        final AbstractSchemaDataProvider provider = SchemaProxy.getProvider(schema);
        final Method[] schemaMethods = provider.getSchemaContext().getSchemaClass().getMethods();
        final Map<String, SchemaSpec<?>> results = new HashMap<>();

        for (Method m : schemaMethods) {
            if (m.getDeclaredAnnotations().length == 0 || m.getDeclaringClass() == Object.class) {
                continue;
            }

            final SpecMethod specMethod = new SpecMethod(m);
            final SchemaSpec<?> spec = provider.createSpec(specMethod, null);

            results.put(specMethod.getName(), spec);

            dataProperties.remove(specMethod.getName());
            dataProperties.remove(specMethod.getDataPropertyName());
        }

        // Add remaining properties that do not have a corresponding
        // SchemaSpec method defined in the Schema
        for (String dataPropertyKey : dataProperties) {
            final SchemaSpec<?> spec = internalSchema.resolveSpec(dataPropertyKey);
            results.put(dataPropertyKey, spec);
        }

        return results;
    }
}
