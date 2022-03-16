package org.instancio.creation.generics;

import org.instancio.pojo.generics.CollectionInteger;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class CollectionIntegerCreationTest extends CreationTestTemplate<CollectionInteger> {

    @Override
    protected void verify(CollectionInteger result) {
        final Collection<Integer> collection = result.getCollection();

        assertThat(collection)
                .isInstanceOf(List.class)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Integer.class);
    }

}
