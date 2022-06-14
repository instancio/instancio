/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Mode;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.Node;
import org.instancio.settings.Keys;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Looks up generators and on-complete callbacks for all descendants
 * of a node for which a null value was generated in order to prevent
 * false positive "unused selector" errors in strict mode.
 */
class GeneratedNullValueListener implements GenerationListener {
    private final ModelContext<?> context;
    private final boolean isLenientMode;

    GeneratedNullValueListener(final ModelContext<?> context) {
        this.context = context;
        this.isLenientMode = context.getSettings().get(Keys.MODE) == Mode.LENIENT;
    }

    @Override
    public void objectCreated(final Node node, @Nullable final Object instance) {
        if (isLenientMode || instance != null) {
            return;
        }

        // A null value was generated for this node.
        // There might be selectors targeting the node's descendants.
        // However, since descendant values may not be generated,
        // this will result in "unused selectors" error in strict mode.
        // Therefore, manually traverse all descendants and mark them as
        // "used" to prevent a false positive error.

        final Queue<Node> queue = new ArrayDeque<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            final Node current = queue.poll();

            // mark as "used"
            context.getGenerator(current);
            context.getCallbacks(current);
            context.getSubtypeMap().getSubtype(current);

            queue.addAll(current.getChildren());
        }
    }
}
