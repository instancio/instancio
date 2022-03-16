package org.instancio.creation.collections.sets;

import org.instancio.pojo.collections.sets.SortedSetLong;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class SortedSetLongCreationTest extends CreationTestTemplate<SortedSetLong> {

    @Override
    protected void verify(SortedSetLong result) {
        assertThat(result.getSet())
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

}
