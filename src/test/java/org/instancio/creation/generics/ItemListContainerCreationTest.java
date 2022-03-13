package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.container.ItemListContainer;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ItemListContainerCreationTest extends CreationTestTemplate<ItemListContainer<Integer, String>> {

    @Override
    protected void verify(ItemListContainer<Integer, String> result) {
        assertThat(result.getItemListX()).isInstanceOf(List.class)
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(Integer.class));

        assertThat(result.getItemListY()).isInstanceOf(List.class)
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(String.class));
    }
}
