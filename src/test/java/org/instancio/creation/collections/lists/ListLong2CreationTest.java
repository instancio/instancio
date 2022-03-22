package org.instancio.creation.collections.lists;

import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListLong2CreationTest extends CreationTestTemplate<List<Long>> {

    @Override
    protected void verify(List<Long> result) {
        assertThat(result)
                .isNotEmpty()
                .hasOnlyElementsOfType(Long.class);
    }

}
