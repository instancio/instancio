package org.instancio.creation.misc;

import org.instancio.pojo.misc.MultipleClassesWithId;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class MultipleClassesWithIdTest extends CreationTestTemplate<MultipleClassesWithId> {

    @Override
    protected void verify(final MultipleClassesWithId result) {
        assertThat(result.getA().getId()).isNotNull();
        assertThat(result.getA().getB().getId()).isNotNull();

        final MultipleClassesWithId.C c = result.getA().getC();
        assertThat(c.getId()).isNotNull();
        assertThat(c.getB().getId()).isNotNull();
        assertThat(c.getD().getId()).isNotNull();
    }
}
