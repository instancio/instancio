package org.instancio.creation.collections.lists;

import org.instancio.pojo.collections.lists.ListLong;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ListLong1CreationTest extends CreationTestTemplate<ListLong> {

    @Override
    protected void verify(ListLong result) {
        assertThat(result.getList())
                .isNotEmpty()
                .hasOnlyElementsOfType(Long.class);
    }

}
