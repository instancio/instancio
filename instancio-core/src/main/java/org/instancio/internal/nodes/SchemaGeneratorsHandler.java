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
package org.instancio.internal.nodes;

import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.internal.schema.MappableSpecsResolver;
import org.instancio.internal.selectors.SchemaSelectors;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaSpec;

import java.util.Map;

class SchemaGeneratorsHandler {

    private final NodeContext nodeContext;
    private final MappableSpecsResolver specsResolver;

    SchemaGeneratorsHandler(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        this.specsResolver = new MappableSpecsResolver();
    }

    void applySchemaGenerators(final InternalNode node) {
        if (node.isIgnored() || (!node.is(NodeKind.POJO) && !node.is(NodeKind.RECORD))) {
            return;
        }
        final Schema schema = nodeContext.getSchema(node);
        if (schema == null) {
            return;
        }

        final Map<String, SchemaSpec<?>> mappableSpecs = specsResolver.getMappableSpecs(schema);

        for (InternalNode child : node.getChildren()) {
            final SchemaSpec<?> spec = mappableSpecs.get(child.getField().getName());

            if (spec != null) {
                final TargetSelector selector = SchemaSelectors.forProperty(child);
                nodeContext.putGenerator(selector, (Generator<?>) spec);
            }
        }
    }
}
