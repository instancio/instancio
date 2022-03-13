package org.instancio.creation.generics;

import org.instancio.pojo.generics.Item;
import org.instancio.testsupport.templates.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ItemCreationTest extends CreationTestTemplate<Item<String>> {

    @Override
    protected void verify(Item<String> result) {
        assertThat(result.getValue()).isInstanceOf(String.class);
    }
}
