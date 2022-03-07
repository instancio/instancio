package org.instancio;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class HierarchyTest {

    private final Hierarchy hierarchy = new Hierarchy();

    @Test
    void getAncestorWithClassSameType() {
        //           R
        //         /   \
        //        A      B
        //       / \    / \
        //      C   D  E   F
        hierarchy.setAncestorOf("R", null);
        hierarchy.setAncestorOf("A", "R");
        hierarchy.setAncestorOf("B", "R");
        hierarchy.setAncestorOf("C", "A");
        hierarchy.setAncestorOf("D", "A");
        hierarchy.setAncestorOf("E", "B");
        hierarchy.setAncestorOf("F", "B");

        assertThat(hierarchy.getAncestorWithClass("R", String.class)).isNull();
        assertThat(hierarchy.getAncestorWithClass("A", String.class)).isEqualTo("R");
        assertThat(hierarchy.getAncestorWithClass("B", String.class)).isEqualTo("R");
        assertThat(hierarchy.getAncestorWithClass("C", String.class)).isEqualTo("A");
        assertThat(hierarchy.getAncestorWithClass("D", String.class)).isEqualTo("A");
        assertThat(hierarchy.getAncestorWithClass("E", String.class)).isEqualTo("B");
        assertThat(hierarchy.getAncestorWithClass("F", String.class)).isEqualTo("B");
    }

    @Test
    void getAncestorWithClassDifferentTypes() {
        //           R
        //         /
        //        1
        //       /
        //      LD
        LocalDate ld = LocalDate.now();
        hierarchy.setAncestorOf("R", null);
        hierarchy.setAncestorOf(1, "R");
        hierarchy.setAncestorOf(ld, 1);

        assertThat(hierarchy.getAncestorWithClass(ld, String.class)).isEqualTo("R");
        assertThat(hierarchy.getAncestorWithClass(ld, Integer.class)).isEqualTo(1);
    }
}
