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

import org.instancio.generator.GeneratorContext;
import org.instancio.schema.SchemaSpec;
import org.instancio.settings.Keys;

import java.util.List;

public abstract class AbstractSchemaDataProvider implements InternalSchema {

    private final SchemaContext<?> schemaContext;
    private final GeneratorContext generatorContext;
    private final String tag;

    protected AbstractSchemaDataProvider(final SchemaContext<?> schemaContext) {
        this.schemaContext = schemaContext;
        this.generatorContext = schemaContext.getGeneratorContext();
        this.tag = generatorContext.getSettings().get(Keys.SCHEMA_TAG_VALUE);
    }

    @Override
    public abstract <T> SchemaSpec<T> createSpec(SpecMethod method, Object[] args);

    public final SchemaContext<?> getSchemaContext() {
        return schemaContext;
    }

    protected final GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    protected final String selectTag(List<String> availableTags) {
        return tag == null
                ? generatorContext.random().oneOf(availableTags)
                : tag;
    }
}
