package org.instancio.creation.collections.sets;

import org.instancio.pojo.collections.sets.TreeSetLong;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeSetLongCreationTest extends CreationTestTemplate<TreeSetLong> {

    @Override
    protected void verify(TreeSetLong result) {
        assertThat(result.getSet())
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

}
