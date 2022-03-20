package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.collections.lists.TwoListsWithItemString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.field;
import static org.instancio.Generators.collection;

class BuiltInGeneratorsTest {

    @Test
    void collectionGenerator() {
        final TwoListsWithItemString result = Instancio.of(TwoListsWithItemString.class)
                .with(field("list1"), collection().type(Vector.class))
                .create();

        assertThat(result.getList1()).isNotEmpty().isInstanceOf(Vector.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result.getList2()).isNotEmpty().isInstanceOf(ArrayList.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }
}
