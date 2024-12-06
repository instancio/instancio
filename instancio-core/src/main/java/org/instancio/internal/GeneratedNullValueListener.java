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
package org.instancio.internal;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.GenerationListener;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Looks up generators and on-complete callbacks for all descendants
 * of a node for which a null value was generated in order to prevent
 * false positive "unused selector" errors in strict mode.
 */
final class GeneratedNullValueListener implements GenerationListener {
    private final ModelContext context;
    private final Queue<InternalNode> queue = new ArrayDeque<>();

    private GeneratedNullValueListener(final ModelContext context) {
        this.context = context;
    }

    static GenerationListener create(final ModelContext context) {
        final boolean isLenient = context.getSettings().get(Keys.MODE) == Mode.LENIENT;

        return isLenient || context.getSelectorMaps().allEmpty()
                ? NOOP_LISTENER
                : new GeneratedNullValueListener(context);
    }

    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        if (!result.containsNull()) {
            return;
        }

        queue.add(node);

        while (!queue.isEmpty()) {
            final InternalNode current = queue.poll();

            if (result.isIgnored()) {
                context.isIgnored(current);
            } else if (result.containsNull()) {
                /*
                 A null result was generated for this node.
                 There might be selectors targeting the node's descendants.
                 However, since descendant values may not be generated,
                 this will result in "unused selectors" error in strict mode.
                 Therefore, manually traverse all descendants and mark them as
                 "used" to prevent a false positive error.
                */
                context.isIgnored(current);
                context.isNullable(current);
                context.getGenerator(current);
                context.getCallbacks(current);
                context.getSubtypeSelectorMap().getSubtype(current);
                // mark destination selectors as used
                context.getAssignments(current);
                // confusing method naming: this is marking _origin_ selectors as used
                context.getAssignmentDestinationSelectors(current);

                queue.addAll(current.getChildren());
            }
        }
    }
}
