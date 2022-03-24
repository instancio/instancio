package org.instancio.creation.misc;

import org.instancio.pojo.basic.IntegerHolderWithPrivateDefaultConstructor;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegerHolderWithPrivateDefaultConstructorCreationTest
        extends CreationTestTemplate<IntegerHolderWithPrivateDefaultConstructor> {

    @Override
    protected void verify(final IntegerHolderWithPrivateDefaultConstructor result) {
        assertThat(result.getWrapper()).isNotNull();
    }
}
