package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.container.PairListContainer;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairListContainerCreationTest extends CreationTestTemplate<PairListContainer<Integer, String>> {

    @Override
    protected void verify(PairListContainer<Integer, String> result) {
        assertThat(result.getPairList()).isInstanceOf(List.class)
                .hasOnlyElementsOfType(Pair.class)
                .allSatisfy(it -> {
                    assertThat(it.getLeft()).isInstanceOf(Integer.class);
                    assertThat(it.getRight()).isInstanceOf(String.class);
                });
    }
}
