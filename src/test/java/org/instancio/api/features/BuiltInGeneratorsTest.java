package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.field;

class BuiltInGeneratorsTest {

    @Test
    void collectionGenerator() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .generate(field("list1"), (gen) -> gen.collection().type(Vector.class))
                .create();

        assertThat(result.getList1()).isNotEmpty().isInstanceOf(Vector.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result.getList2()).isNotEmpty().isInstanceOf(ArrayList.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }
}
