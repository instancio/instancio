package org.instancio.creation.collections.lists;

import org.instancio.pojo.collections.lists.ListListString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListListStringCreationTest extends CreationTestTemplate<ListListString> {

    @Override
    protected void verify(ListListString result) {
        assertThat(result.getNested())
                .hasSize(Constants.COLLECTION_SIZE)
                .allSatisfy(nestedListElement -> {
                    assertThat(nestedListElement)
                            .hasSize(Constants.COLLECTION_SIZE)
                            .hasOnlyElementsOfType(String.class);
                });
    }

}
