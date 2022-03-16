package org.instancio.creation.generics;

import org.instancio.pojo.generics.ListWithoutType;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListWithoutTypeCreationTest extends CreationTestTemplate<ListWithoutType> {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void verify(ListWithoutType result) {
        final List list = result.getList();

        assertThat(list)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Object.class);
    }

}
