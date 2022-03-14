package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.container.PairArrayContainer;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairArrayContainerCreationTest extends CreationTestTemplate<PairArrayContainer<Integer, String>> {

    @Override
    protected void verify(PairArrayContainer<Integer, String> result) {
        assertThat(result.getPairArray())
                .isInstanceOf(Pair[].class)
                .allSatisfy(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(Integer.class);
                    assertThat(pair.getRight()).isInstanceOf(String.class);
                });
    }
}
