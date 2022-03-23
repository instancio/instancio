package org.instancio.creation.collections.sets;

import org.instancio.pojo.collections.sets.NavigableSetLong;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class NavigableSetLongCreationTest extends CreationTestTemplate<NavigableSetLong> {

    @Override
    protected void verify(NavigableSetLong result) {
        assertThat(result.getSet())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

}
