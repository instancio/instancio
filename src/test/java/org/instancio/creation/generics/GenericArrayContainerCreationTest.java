package org.instancio.creation.generics;

import org.instancio.pojo.generics.container.GenericArrayContainer;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class GenericArrayContainerCreationTest extends CreationTestTemplate<GenericArrayContainer<Integer, String>> {

    @Override
    protected void verify(GenericArrayContainer<Integer, String> result) {
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
