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

import org.instancio.internal.AncestorTree.AncestorTreeNode;
import org.instancio.internal.nodes.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AncestorTreeTest {

    private final AncestorTree detector = new AncestorTree();

    @Mock
    private Node nodeR, nodeA, nodeB;

    @Test
    void verifyAncestorMapping() {
        //           R
        //         /   \
        //        A      B
        //       / \    / \
        //      C   D  E   F

        when(nodeA.getParent()).thenReturn(nodeR);
        when(nodeB.getParent()).thenReturn(nodeR);

        final AncestorTreeNode inodeR = new AncestorTreeNode("R", nodeR);
        final AncestorTreeNode inodeA = new AncestorTreeNode("A", nodeA);
        final AncestorTreeNode inodeB = new AncestorTreeNode("B", nodeB);

        detector.setObjectAncestor("A", inodeR);
        detector.setObjectAncestor("B", inodeR);
        detector.setObjectAncestor("C", inodeA);
        detector.setObjectAncestor("D", inodeA);
        detector.setObjectAncestor("E", inodeB);
        detector.setObjectAncestor("F", inodeB);

        assertThat(detector.getObjectAncestor("R", null)).isNull();
        assertThat(detector.getObjectAncestor("A", nodeR)).isSameAs(inodeR);
        assertThat(detector.getObjectAncestor("B", nodeR)).isSameAs(inodeR);
        assertThat(detector.getObjectAncestor("C", nodeA)).isSameAs(inodeA);
        assertThat(detector.getObjectAncestor("D", nodeA)).isSameAs(inodeA);
        assertThat(detector.getObjectAncestor("E", nodeB)).isSameAs(inodeB);
        assertThat(detector.getObjectAncestor("F", nodeB)).isSameAs(inodeB);
    }

}
