package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.PairAString;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairAStringCreationTest extends CreationTestTemplate<PairAString<UUID>> {

    @Override
    protected void verify(PairAString<UUID> result) {
        assertThat(result.getPairAString()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(UUID.class);
                    assertThat(pair.getRight()).isInstanceOf(String.class);
                });
    }

}
