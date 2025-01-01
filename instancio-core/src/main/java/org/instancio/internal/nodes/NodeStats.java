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
package org.instancio.internal.nodes;

import org.instancio.internal.util.Format;

import java.util.List;

import static org.instancio.internal.util.Constants.NL;

public final class NodeStats {
    private static final int SB_SIZE = 10_000;
    private static final int NUM_NODES = 0;
    private static final int MAX_DEPTH = 1;

    private final String treeString;
    private final int totalNodes;
    private final int height;

    private NodeStats(final String treeString, final int totalNodes, final int height) {
        this.treeString = treeString;
        this.totalNodes = totalNodes;
        this.height = height;
    }

    public static NodeStats compute(final InternalNode rootNode) {
        final int[] stats = new int[2];
        stats[NUM_NODES] = 1; // root node

        final StringBuilder sb = new StringBuilder(SB_SIZE);
        sb.append(Format.formatAsTreeNode(rootNode)).append(NL);
        appendTreeLine(rootNode, sb, stats, "");
        return new NodeStats(sb.toString(), stats[NUM_NODES], stats[MAX_DEPTH]);
    }

    // Based on: https://github.com/kddnewton/tree
    private static void appendTreeLine(
            final InternalNode node, final StringBuilder sb, final int[] stats, final String prefix) {

        final List<InternalNode> children = node.getChildren();
        final int size = children.size();
        stats[NUM_NODES] += size;
        stats[MAX_DEPTH] = Math.max(stats[MAX_DEPTH], node.getDepth());

        for (int i = 0; i < size; i++) {
            final InternalNode n = children.get(i);

            if (i == size - 1) {
                sb.append(prefix).append(" └──").append(Format.formatAsTreeNode(n)).append(NL);
                appendTreeLine(n, sb, stats, prefix + "    ");
            } else {
                sb.append(prefix).append(" ├──").append(Format.formatAsTreeNode(n)).append(NL);
                appendTreeLine(n, sb, stats, prefix + " │  ");
            }
        }
    }

    public String getTreeString() {
        return treeString;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public int getHeight() {
        return height;
    }
}
