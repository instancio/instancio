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
import org.instancio.internal.nodes.Node;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.Nullable;

class ArrayElementNodePopulationFilter implements NodePopulationFilter {

    private final ModelContext<?> context;

    ArrayElementNodePopulationFilter(final ModelContext<?> context) {
        this.context = context;
    }

    @Override
    public boolean shouldSkip(final Node elementNode,
                              final AfterGenerate afterGenerate,
                              @Nullable final Object currentElementValue) {

        if (afterGenerate == AfterGenerate.DO_NOT_MODIFY) {
            return true;
        }
        if (afterGenerate == AfterGenerate.POPULATE_ALL) {
            return false;
        }

        // For APPLY_SELECTORS and remaining values, if there is at least
        // one matching selector for this node, then it should not be skipped
        if (context.getGenerator(elementNode).isPresent()) {
            return false;
        }

        if (afterGenerate == AfterGenerate.POPULATE_NULLS) {
            return elementNode.getRawType().isPrimitive() || currentElementValue != null;
        }

        if (afterGenerate == AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES) {
            return ReflectionUtils.neitherNullNorPrimitiveWithDefaultValue(
                    elementNode.getRawType(),
                    currentElementValue);
        }
        return true;
    }

}
