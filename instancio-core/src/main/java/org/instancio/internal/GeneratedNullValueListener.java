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
import org.instancio.internal.nodes.ArrayNode;
import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.nodes.MapNode;
import org.instancio.internal.nodes.Node;
import org.instancio.settings.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Looks up generators and on-complete callbacks for all descendants
 * of a node for which a null value was generated in order to prevent
 * false positive "unused selector" errors in strict mode.
 */
class GeneratedNullValueListener implements GenerationListener {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratedNullValueListener.class);
    private static final int CYCLIC_NODE_LOOP_LIMIT = 50_000;

    private final Map<Node, Boolean> seen = new IdentityHashMap<>();
    private final ModelContext<?> context;
    private final boolean isLenientMode;

    public GeneratedNullValueListener(final ModelContext<?> context) {
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

        int i = 0;
        for (; i < CYCLIC_NODE_LOOP_LIMIT && !queue.isEmpty(); i++) {
            final Node current = queue.poll();

            if (seen.putIfAbsent(current, true) != null) {
                continue;
            }

            // mark as "used"
            context.getGenerator(current);
            context.getCallbacks(current);

            queue.addAll(current.getChildren());
            if (current instanceof CollectionNode) {
                queue.add(((CollectionNode) current).getElementNode());
            } else if (current instanceof ArrayNode) {
                queue.add(((ArrayNode) current).getElementNode());
            } else if (current instanceof MapNode) {
                queue.add(((MapNode) current).getKeyNode());
                queue.add(((MapNode) current).getValueNode());
            }
        }

        if (i == CYCLIC_NODE_LOOP_LIMIT) {
            LOG.debug("Reached iteration limit of {} marking selectors as 'used'. " +
                    "This may result in a false positive 'unused selector' warning.", CYCLIC_NODE_LOOP_LIMIT);
        }
    }
}
