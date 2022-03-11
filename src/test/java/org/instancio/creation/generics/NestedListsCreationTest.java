package org.instancio.creation.generics;

import org.instancio.pojo.collections.NestedLists;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class NestedListsCreationTest extends CreationTestTemplate<NestedLists> {

    @Override
    protected void verify(NestedLists result) {
        assertThat(result.getNested())
                .hasSize(Constants.COLLECTION_SIZE)
                .allSatisfy(nestedListElement -> {
                    assertThat(nestedListElement)
                            .hasSize(Constants.COLLECTION_SIZE)
                            .hasOnlyElementsOfType(String.class);
                });
    }

}
