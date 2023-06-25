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
package org.instancio.internal;

import org.instancio.generator.AfterGenerate;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;

final class NodeFilter implements NodePopulationFilter {

    private final NodePopulationFilter arrayNodeFilter = new ArrayElementNodePopulationFilter();
    private final NodePopulationFilter fieldNodeFilter = new FieldNodePopulationFilter();
    private final ModelContext<?> context;

    NodeFilter(final ModelContext<?> context) {
        this.context = context;
    }

    @Override
    public boolean shouldSkip(final InternalNode node, final AfterGenerate afterGenerate, final Object owner) {
        if (node.isIgnored() || afterGenerate == AfterGenerate.DO_NOT_MODIFY) {
            return true;
        }

        // For APPLY_SELECTORS and remaining actions, if there is at least
        // one matching selector for this node, then it should not be skipped
        if (context.getGenerator(node).isPresent()) {
            return false;
        }

        final InternalNode parent = node.getParent();

        final NodePopulationFilter filter = parent.is(NodeKind.ARRAY)
                ? arrayNodeFilter : fieldNodeFilter;

        return filter.shouldSkip(node, afterGenerate, owner);
    }

}
