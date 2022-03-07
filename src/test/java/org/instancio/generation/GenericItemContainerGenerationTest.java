package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.pojo.generics.container.Pair;
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

        assertThat(result.getItemValueX()).isInstanceOf(GenericItem.class);
        assertThat(result.getItemListX()).isInstanceOf(List.class);

        assertThat(result.getItemValueY()).isInstanceOf(GenericItem.class);
        assertThat(result.getItemListY()).isInstanceOf(List.class);

        assertThat(result.getPairValue()).isInstanceOf(Pair.class);
        assertThat(result.getPairArray()).isInstanceOf(Pair[].class);
        assertThat(result.getPairList()).isInstanceOf(List.class);
    }
}
