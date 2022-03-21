package org.instancio.creation.collections.lists;

import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class TwoListsOfItemStringCreationTest extends CreationTestTemplate<TwoListsOfItemString> {

    @Override
    protected void verify(TwoListsOfItemString result) {
        assertThat(result.getList1())
                .hasOnlyElementsOfType(Item.class)
                .isNotEmpty();

        assertThat(result.getList2())
                .hasOnlyElementsOfType(Item.class)
                .isNotEmpty();
    }

}
