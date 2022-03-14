package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ItemContainerCreationTest extends CreationTestTemplate<ItemContainer<Integer, String>> {

    @Override
    protected void verify(ItemContainer<Integer, String> result) {
        assertThat(result.getItemValueX()).isInstanceOf(Item.class)
                .extracting(Item::getValue)
                .isInstanceOf(Integer.class);

        assertThat(result.getItemValueY()).isInstanceOf(Item.class)
                .extracting(Item::getValue)
                .isInstanceOf(String.class);
    }
}
