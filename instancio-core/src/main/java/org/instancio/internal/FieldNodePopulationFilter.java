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

import org.instancio.exception.InstancioException;
import org.instancio.generator.AfterGenerate;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.ExceptionHandler;
import org.instancio.internal.util.ReflectionUtils;

class FieldNodePopulationFilter implements NodePopulationFilter {

    private final ModelContext<?> context;

    FieldNodePopulationFilter(final ModelContext<?> context) {
        this.context = context;
    }

    @Override
    public boolean shouldSkip(final InternalNode fieldNode,
                              final AfterGenerate afterGenerate,
                              final Object objectContainingField) {

        if (fieldNode.is(NodeKind.IGNORED)) {
            return true;
        }
        if (afterGenerate == AfterGenerate.DO_NOT_MODIFY) {
            return true;
        }

        // For APPLY_SELECTORS and remaining actions, if there is at least
        // one matching selector for this node, then it should not be skipped
        if (context.getGenerator(fieldNode).isPresent()) {
            return false;
        }
        if (afterGenerate == AfterGenerate.POPULATE_NULLS) {
            return ReflectionUtils.hasNonNullValue(fieldNode.getField(), objectContainingField);
        }
        if (afterGenerate == AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES) {
            if (fieldNode.getField() == null) {
                ExceptionHandler.conditionalFailOnError(() -> {
                    throw new InstancioException("Node has a null field: " + fieldNode);
                });
            }
            return ReflectionUtils.hasNonNullOrNonDefaultPrimitiveValue(
                    fieldNode.getField(), objectContainingField);
        }

        return afterGenerate != AfterGenerate.POPULATE_ALL;
    }
}
