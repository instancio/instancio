package org.instancio.creation.generics;

import org.instancio.pojo.generics.FooContainer;
import org.instancio.testsupport.base.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class FooContainerCreationTest extends CreationTestTemplate<FooContainer> {

    @Override
    protected void verify(FooContainer result) {
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getFooValue()).isInstanceOf(String.class);
        assertThat(result.getItem().getOtherFooValue()).isNotNull();
    }

}
