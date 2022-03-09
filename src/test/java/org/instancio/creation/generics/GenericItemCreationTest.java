package org.instancio.creation.generics;

import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.testsupport.base.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class GenericItemCreationTest extends CreationTestTemplate<GenericItem<String>> {

    @Override
    protected void verify(GenericItem<String> result) {
        assertThat(result.getValue()).isInstanceOf(String.class);
    }
}
