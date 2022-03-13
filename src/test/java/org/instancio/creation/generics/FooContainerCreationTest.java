package org.instancio.creation.generics;

import org.instancio.pojo.generics.foobarbaz.FooContainer;
import org.instancio.testsupport.templates.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class FooContainerCreationTest extends CreationTestTemplate<FooContainer> {

    @Override
    protected void verify(FooContainer result) {
        assertThat(result.getStringFoo()).isNotNull();
        assertThat(result.getStringFoo().getFooValue()).isInstanceOf(String.class);
        assertThat(result.getStringFoo().getOtherFooValue()).isNotNull();
    }

}
