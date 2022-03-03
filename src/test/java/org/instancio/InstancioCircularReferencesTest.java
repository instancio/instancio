package org.instancio;

import org.instancio.pojo.circular.BidirectionalOneToOne;
import org.instancio.pojo.circular.IndirectCircularRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InstancioCircularReferencesTest {

    @Test
    void indirectCircularRef() {
        IndirectCircularRef.A a = Instancio.of(IndirectCircularRef.A.class).create();

        assertThat(a.getB()).isNotNull();
        assertThat(a.getB().getC()).isNotNull();
        assertThat(a.getB().getC().getA()).isNull();
    }


    @Test
    void parentChildCreateParent() {
        BidirectionalOneToOne.Parent parent = Instancio.of(BidirectionalOneToOne.Parent.class).create();

        assertThat(parent.getChild()).isNotNull();
        assertThat(parent.getChild().getParent()).as("Do not populate parent reference").isNull();
    }

    @Test
    void parentChildCreateChild() {
        BidirectionalOneToOne.Child child = Instancio.of(BidirectionalOneToOne.Child.class).create();

        assertThat(child.getParent()).isNotNull();
        assertThat(child.getParent().getChild())
                .as("Do not populate child reference")
                .isNull();
    }

}
