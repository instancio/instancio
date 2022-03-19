package org.instancio;

import org.instancio.AncestorTree.InstanceNode;
import org.instancio.model.Node;
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

        final InstanceNode inodeR = new InstanceNode("R", nodeR);
        final InstanceNode inodeA = new InstanceNode("A", nodeA);
        final InstanceNode inodeB = new InstanceNode("B", nodeB);

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
