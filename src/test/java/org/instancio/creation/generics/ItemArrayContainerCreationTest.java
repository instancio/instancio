package org.instancio.creation.generics;

import org.instancio.pojo.generics.Item;
import org.instancio.pojo.generics.container.ItemArrayContainer;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ItemArrayContainerCreationTest extends CreationTestTemplate<ItemArrayContainer<Integer, String>> {

    @Override
    protected void verify(ItemArrayContainer<Integer, String> result) {
        assertThat(result.getItemArrayX())
                .hasSize(Constants.ARRAY_SIZE)
                .isInstanceOf(Item[].class)
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(Integer.class));

        assertThat(result.getItemArrayY())
                .hasSize(Constants.ARRAY_SIZE)
                .isInstanceOf(Item[].class)
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(String.class));
    }
}
