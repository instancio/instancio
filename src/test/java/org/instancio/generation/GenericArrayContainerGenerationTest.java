package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.container.GenericArrayContainer;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class GenericArrayContainerGenerationTest {

    @Test
    void generate() {
        GenericArrayContainer<?, ?> result = Instancio.of(GenericArrayContainer.class)
                .withType(Integer.class, String.class)
                .create();

        assertThat(result.getItemArrayX())
                .hasSize(Constants.ARRAY_SIZE)
                .isInstanceOf(GenericItem[].class)
                .hasOnlyElementsOfType(GenericItem.class)
                .extracting(it -> assertThat(it.getValue()).isInstanceOf(Integer.class));

        assertThat(result.getItemArrayY())
                .hasSize(Constants.ARRAY_SIZE)
                .isInstanceOf(GenericItem[].class)
                .hasOnlyElementsOfType(GenericItem.class)
                .extracting(it -> assertThat(it.getValue()).isInstanceOf(String.class));
    }
}
