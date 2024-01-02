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
package org.instancio.nodes;

import org.instancio.internal.nodes.InternalNode;
import org.instancio.test.support.pojo.cyclic.ClassesABCWithCrossReferences;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.NodeUtils.getChildNode;

@CyclicTag
class ClassesABCWithCrossReferencesNodeTest extends NodeTestTemplate<ClassesABCWithCrossReferences> {

    private static final int EXPECTED_MAX_DEPTH = 5;

    @Override
    protected void verify(InternalNode rootNode) {
        assertNode(rootNode)
                .hasTargetClass(ClassesABCWithCrossReferences.class)
                .hasChildrenOfSize(3);

        final Stats stats = new Stats();
        assertNodeRecursively(rootNode, 0, stats);

        final int totalNodes = Arrays.stream(stats.nodesAtDepth).sum();
        assertThat(totalNodes).isEqualTo(49);

        assertThat(stats.maxDepth).isEqualTo(4);
        assertThat(stats.nodesAtDepth[0]).isEqualTo(1);
        assertThat(stats.nodesAtDepth[1]).isEqualTo(3);
        assertThat(stats.nodesAtDepth[2]).isEqualTo(9);
        assertThat(stats.nodesAtDepth[3]).isEqualTo(18);
        assertThat(stats.nodesAtDepth[4]).isEqualTo(18);
    }

    private void assertNodeRecursively(final InternalNode node, final int expectedDepth, final Stats stats) {
        if (node == null) return;

        stats.maxDepth = Math.max(stats.maxDepth, node.getDepth());
        stats.nodesAtDepth[node.getDepth()]++;
        assertNode(node).hasDepth(expectedDepth);

        assertNodeRecursively(getChildNode(node, "objectA"), expectedDepth + 1, stats);
        assertNodeRecursively(getChildNode(node, "objectB"), expectedDepth + 1, stats);
        assertNodeRecursively(getChildNode(node, "objectC"), expectedDepth + 1, stats);
    }

    private static class Stats {
        int maxDepth;
        final int[] nodesAtDepth = new int[EXPECTED_MAX_DEPTH];
    }
}