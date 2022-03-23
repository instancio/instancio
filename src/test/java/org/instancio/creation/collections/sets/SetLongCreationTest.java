package org.instancio.creation.collections.sets;

import org.instancio.pojo.collections.sets.SetLong;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class SetLongCreationTest extends CreationTestTemplate<SetLong> {

    @Override
    protected void verify(SetLong result) {
        assertThat(result.getSet())
                .isNotEmpty()
                .hasOnlyElementsOfType(Long.class);
    }

}
