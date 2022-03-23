package org.instancio.creation.generics;

import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListOfOuterMidInnerStringCreationTest extends CreationTestTemplate<ListOfOuterMidInnerString> {

    @Override
    protected void verify(ListOfOuterMidInnerString result) {
        assertThat(result.getRootList()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        result.getRootList().forEach(outer -> {
            assertThat(outer).isNotNull();
            assertThat(outer.getOuterList()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

            outer.getOuterList().forEach(mid -> {
                assertThat(mid).isNotNull();
                assertThat(mid.getMidList()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

                mid.getMidList().forEach(inner -> {
                    assertThat(inner).isNotNull();
                    assertThat(inner.getInnerList())
                            .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                            .hasOnlyElementsOfType(String.class);
                });
            });
        });
    }
}
