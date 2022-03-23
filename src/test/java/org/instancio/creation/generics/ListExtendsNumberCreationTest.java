package org.instancio.creation.generics;

import org.instancio.pojo.generics.ListExtendsNumber;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListExtendsNumberCreationTest extends CreationTestTemplate<ListExtendsNumber> {

    @Override
    protected void verify(ListExtendsNumber result) {
        final List<? extends Number> list = result.getList();

        assertThat(list)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Number.class);
    }

}
