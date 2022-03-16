package org.instancio.creation.collections.sets;

import org.instancio.pojo.collections.sets.ConcurrentSkipListSetLong;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrentSkipListSetLongCreationTest extends CreationTestTemplate<ConcurrentSkipListSetLong> {

    @Override
    protected void verify(ConcurrentSkipListSetLong result) {
        assertThat(result.getSet())
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

}
