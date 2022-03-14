package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.container.PairContainer;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairContainerCreationTest extends CreationTestTemplate<PairContainer<Integer, String>> {

    @Override
    protected void verify(PairContainer<Integer, String> result) {
        assertThat(result.getPairValue()).isInstanceOf(Pair.class);
    }
}
