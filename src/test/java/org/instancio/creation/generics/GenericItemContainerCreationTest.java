package org.instancio.creation.generics;

import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.testsupport.templates.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class GenericItemContainerCreationTest extends CreationTestTemplate<GenericItemContainer<Integer, String>> {

    @Override
    protected void verify(GenericItemContainer<Integer, String> result) {
        assertThat(result.getItemValueX()).isInstanceOf(GenericItem.class);
        assertThat(result.getItemListX()).isInstanceOf(List.class)
                .hasOnlyElementsOfType(GenericItem.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(Integer.class));

        assertThat(result.getItemValueY()).isInstanceOf(GenericItem.class);
        assertThat(result.getItemListY()).isInstanceOf(List.class)
                .hasOnlyElementsOfType(GenericItem.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(String.class));

        assertThat(result.getPairValue()).isInstanceOf(Pair.class);
        assertThat(result.getPairArray()).isInstanceOf(Pair[].class);
        assertThat(result.getPairList()).isInstanceOf(List.class)
                .hasOnlyElementsOfType(Pair.class)
                .allSatisfy(it -> {
                    assertThat(it.getLeft()).isInstanceOf(Integer.class);
                    assertThat(it.getRight()).isInstanceOf(String.class);
                });
    }
}
