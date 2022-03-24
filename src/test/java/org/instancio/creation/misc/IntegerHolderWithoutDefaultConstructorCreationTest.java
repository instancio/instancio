package org.instancio.creation.misc;

import org.instancio.pojo.basic.IntegerHolderWithoutDefaultConstructor;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegerHolderWithoutDefaultConstructorCreationTest
        extends CreationTestTemplate<IntegerHolderWithoutDefaultConstructor> {

    @Override
    protected void verify(final IntegerHolderWithoutDefaultConstructor result) {
        assertThat(result.getWrapper()).isNotNull();
    }
}
