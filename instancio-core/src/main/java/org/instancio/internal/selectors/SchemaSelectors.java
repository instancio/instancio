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
package org.instancio.internal.selectors;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Constants;

import java.util.Collections;
import java.util.function.Predicate;

@InternalApi
public final class SchemaSelectors {

    private static final class SchemaSelector extends PredicateSelectorImpl {
        private SchemaSelector(final Predicate<InternalNode> predicate, final String description) {
            super(Constants.SCHEMA_SELECTOR_PRIORITY,
                    predicate,
                    Collections.emptyList(),
                    /* depth = */ null,
                    /* isLenient = */ true,
                    /* isHiddenFromVerboseOutput = */ true,
                    description,
                    new Throwable());
        }

        @Override
        public String toString() {
            return String.format("%s.%s.lenient() [internal]",
                    SchemaSelectors.class.getSimpleName(),
                    getApiInvocationDescription());
        }
    }

    public static InternalSelector forProperty(final InternalNode fieldNode) {
        return new SchemaSelector(node -> fieldNode.getParent().equals(node.getParent())
                && node.getField().getName().equals(fieldNode.getField().getName()),
                String.format("forProperty(\"%s\")", fieldNode.getField().getName()));
    }

    private SchemaSelectors() {
        // non-instantiable
    }
}