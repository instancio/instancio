package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class ListOfOuterMidInnerStringGenerationTest {

    @Test
    void generate() {
        ListOfOuterMidInnerString result = Instancio.of(ListOfOuterMidInnerString.class).create();

        assertThat(result).isNotNull();
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
