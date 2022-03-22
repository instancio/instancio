package org.instancio.creation.generics;

import org.instancio.pojo.generics.ListWithWildcard;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListWithWildcardCreationTest extends CreationTestTemplate<ListWithWildcard> {

    @Override
    protected void verify(ListWithWildcard result) {
        final List<?> list = result.getList();

        assertThat(list)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Object.class);
    }

}
