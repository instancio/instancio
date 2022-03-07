package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class GenericItemContainerGenerationTest {

    @Test
    void generate() {
        GenericItemContainer<?, ?> result = Instancio.of(GenericItemContainer.class)
                .withType(Integer.class, String.class)
                .create();

        // FIXME fill out test and fix bugs
        // TODO split GenericItemContainer into smaller classes

        System.out.println(result);

        assertThat(result.getItemValueL()).isInstanceOf(GenericItem.class);
        assertThat(result.getItemArrayL())
                .hasSize(Constants.ARRAY_SIZE)
                .isInstanceOf(GenericItem[].class)
                .hasOnlyElementsOfType(Integer.class);

        assertThat(result.getItemListL()).isInstanceOf(List.class);

        assertThat(result.getItemValueR()).isInstanceOf(GenericItem.class);
        assertThat(result.getItemArrayR()).isInstanceOf(GenericItem[].class);
        assertThat(result.getItemListR()).isInstanceOf(List.class);

        assertThat(result.getPairValue()).isInstanceOf(Pair.class);
        assertThat(result.getPairArray()).isInstanceOf(Pair[].class);
        assertThat(result.getPairList()).isInstanceOf(List.class);
    }
}
