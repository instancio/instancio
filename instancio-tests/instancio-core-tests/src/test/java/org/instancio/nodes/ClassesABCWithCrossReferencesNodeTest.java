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
package org.instancio.nodes;

import org.instancio.internal.nodes.Node;
import org.instancio.test.support.pojo.cyclic.ClassesABCWithCrossReferences;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.NodeTestTemplate;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.NodeUtils.getChildNode;

@CyclicTag
class ClassesABCWithCrossReferencesNodeTest extends NodeTestTemplate<ClassesABCWithCrossReferences> {

    private static final int EXPECTED_MAX_DEPTH = 10;

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasTargetClass(ClassesABCWithCrossReferences.class)
                .hasChildrenOfSize(3);

        final Stats stats = new Stats();
        assertNodeRecursively(rootNode, 0, stats);

        // Sample path:
        // Root: ClassesABCWithCrossReferences[0]
        // > ClassesABCWithCrossReferences.objectA[1]
        // > ObjectA.objectA[2]
        // > ObjectA.objectB[3]
        // > ObjectB.objectA[4]
        // > ObjectA.objectC[5]
        // > ObjectC.objectB[6]
        // > ObjectB.objectB[7]
        // > ObjectB.objectC[8]
        // > ObjectC.objectC[9]
        // > ObjectC.objectA[10]
        // > ObjectC.objectB - null since already occurred at depth [6]
        assertThat(stats.maxDepth).isEqualTo(EXPECTED_MAX_DEPTH);

        // Note: the expected counts were put in after running the test.
        // Need a calculation to confirm whether these expectations are actually correct
        // (some nodes were discarded due to cycles)
        assertThat(Arrays.stream(stats.nodesAtDepth).sum()).isEqualTo(1885);

        assertThat(stats.nodesAtDepth[0]).isEqualTo(1);
        assertThat(stats.nodesAtDepth[1]).isEqualTo(3);
        assertThat(stats.nodesAtDepth[2]).isEqualTo(9);
        assertThat(stats.nodesAtDepth[3]).isEqualTo(24);
        assertThat(stats.nodesAtDepth[4]).isEqualTo(60);
        assertThat(stats.nodesAtDepth[5]).isEqualTo(126);
        assertThat(stats.nodesAtDepth[6]).isEqualTo(234);
        assertThat(stats.nodesAtDepth[7]).isEqualTo(348);
        assertThat(stats.nodesAtDepth[8]).isEqualTo(432);
        assertThat(stats.nodesAtDepth[9]).isEqualTo(432);
        assertThat(stats.nodesAtDepth[10]).isEqualTo(216);
    }

    private void assertNodeRecursively(final Node node, final int expectedDepth, final Stats stats) {
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
        final int[] nodesAtDepth = new int[EXPECTED_MAX_DEPTH + 1];
    }
}