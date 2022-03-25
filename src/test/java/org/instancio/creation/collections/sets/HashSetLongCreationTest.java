package org.instancio.creation.collections.sets;

import org.instancio.pojo.collections.sets.HashSetLong;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class HashSetLongCreationTest extends CreationTestTemplate<HashSetLong> {

    @Override
    protected void verify(HashSetLong result) {
        assertThat(result.getSet())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

}
