/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.jetbrains.annotations.Nullable;

import static org.instancio.internal.util.ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue;
import static org.instancio.internal.util.ReflectionUtils.hasNonNullValue;

final class NodeFilter implements NodePopulationFilter {

    private final NodePopulationFilter arrayNodeFilter = new ArrayElementNodePopulationFilter();
    private final boolean isMethodAssignment;
    private final boolean overwriteExistingValues;
    private final OnSetMethodNotFound onSetMethodNotFound;
    private final ModelContext context;

    NodeFilter(final ModelContext context) {
        this.context = context;
        this.isMethodAssignment = context.getSettings().get(Keys.ASSIGNMENT_TYPE) == AssignmentType.METHOD;
        this.onSetMethodNotFound = context.getSettings().get(Keys.ON_SET_METHOD_NOT_FOUND);
        this.overwriteExistingValues = context.getSettings().get(Keys.OVERWRITE_EXISTING_VALUES);
    }

    @Override
    @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public NodeFilterResult filter(
            final InternalNode node,
            final AfterGenerate afterGenerate,
            @Nullable final Object owner) {

        if (node.isIgnored() || afterGenerate == AfterGenerate.DO_NOT_MODIFY) {
            return NodeFilterResult.SKIP;
        }

        // If method assignment is enabled and there's no setter method, skip the node
        if (isMethodAssignment
                && node.getSetter() == null
                && node.getField() != null
                && onSetMethodNotFound == OnSetMethodNotFound.IGNORE) {
            return NodeFilterResult.SKIP;
        }

        // Record fields are immutable, so we cannot generate and assign a new value.
        // However, a record might contain a POJO, in which case we can populate it
        if (node.getParent().is(NodeKind.RECORD)) {
            return NodeFilterResult.POPULATE;
        }

        // For APPLY_SELECTORS and remaining actions, if there is at least
        // one matching selector for this node, then it should not be skipped
        if (context.getGenerator(node).isPresent() || !context.getAssignments(node).isEmpty()) {
            return NodeFilterResult.GENERATE;
        }

        if (node.getParent().is(NodeKind.ARRAY)) {
            return arrayNodeFilter.filter(node, afterGenerate, owner);
        }

        if (!overwriteExistingValues && node.getField() != null
                && hasNonNullOrNonDefaultPrimitiveValue(node.getField(), owner)) {
            return getResultForNode(node);
        }

        if (afterGenerate == AfterGenerate.POPULATE_NULLS) {
            if (node.getField() != null && hasNonNullValue(node.getField(), owner)) {
                return getResultForNode(node);
            }
            return NodeFilterResult.GENERATE;
        }

        if (afterGenerate == AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES) {
            if (node.getField() != null && hasNonNullOrNonDefaultPrimitiveValue(node.getField(), owner)) {
                return getResultForNode(node);
            }
            return NodeFilterResult.GENERATE;
        }

        return afterGenerate == AfterGenerate.POPULATE_ALL
                ? NodeFilterResult.GENERATE
                : NodeFilterResult.POPULATE;
    }

    private static NodeFilterResult getResultForNode(final InternalNode node) {
        return isPojoOrDataStructure(node) ? NodeFilterResult.POPULATE : NodeFilterResult.SKIP;
    }

    private static boolean isPojoOrDataStructure(final InternalNode node) {
        return node.is(NodeKind.POJO)
                || node.is(NodeKind.COLLECTION)
                || node.is(NodeKind.MAP)
                || node.is(NodeKind.ARRAY);
    }
}
