package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.collections.lists.ListLong;
import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.field;

class SubtypeMappingTest {

    @Test
    @DisplayName("Subtype mapping from List to Vector")
    void subtypeMappingWithSingleList() {
        final ListLong result = Instancio.of(ListLong.class)
                .map(List.class, Vector.class)
                .create();

        assertThat(result.getList()).isNotEmpty().isInstanceOf(Vector.class);
    }

    @Test
    @DisplayName("Given subtype mapping from List to Vector and a generator, the generator takes precedence")
    void subtypeMappingWithMultipleLists() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .map(List.class, Vector.class)
                .generate(field("list1"), gen -> gen.collection().type(LinkedList.class))
                .create();

        assertThat(result.getList1()).isNotEmpty().isInstanceOf(LinkedList.class);
        assertThat(result.getList2()).isNotEmpty().isInstanceOf(Vector.class);
    }
}
