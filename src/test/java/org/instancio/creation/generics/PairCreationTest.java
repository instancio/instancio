package org.instancio.creation.generics;

import org.instancio.pojo.generics.container.Pair;
import org.instancio.testsupport.base.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairCreationTest extends CreationTestTemplate<Pair<String, Integer>> {

    @Override
    protected void verify(Pair<String, Integer> result) {
        assertThat(result.getLeft()).isInstanceOf(String.class);
        assertThat(result.getRight()).isInstanceOf(Integer.class);
    }
}
