package org.instancio.creation.generics;

import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.base.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListOfOuterMidInnerStringCreationTest extends CreationTestTemplate<ListOfOuterMidInnerString> {

    @Override
    protected void verify(ListOfOuterMidInnerString result) {
        assertThat(result.getListOfOuter()).hasSize(Constants.COLLECTION_SIZE);

        result.getListOfOuter().forEach(outer -> {
            assertThat(outer).isNotNull();
            assertThat(outer.getOuterList()).hasSize(Constants.COLLECTION_SIZE);

            outer.getOuterList().forEach(mid -> {
                assertThat(mid).isNotNull();
                assertThat(mid.getMidList()).hasSize(Constants.COLLECTION_SIZE);

                mid.getMidList().forEach(inner -> {
                    assertThat(inner).isNotNull();
                    assertThat(inner.getInnerList())
                            .hasSize(Constants.COLLECTION_SIZE)
                            .hasOnlyElementsOfType(String.class);
                });
            });
        });
    }
}
