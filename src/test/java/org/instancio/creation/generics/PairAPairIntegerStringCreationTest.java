package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.PairAPairIntegerString;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairAPairIntegerStringCreationTest extends CreationTestTemplate<PairAPairIntegerString<UUID>> {

    @Override
    protected void verify(PairAPairIntegerString<UUID> result) {
        assertThat(result.getPairAPairIntegerString()).isInstanceOf(Pair.class).satisfies(pair -> {
            assertThat(pair.getLeft()).isInstanceOf(UUID.class);
            assertThat(pair.getRight()).isInstanceOf(Pair.class).satisfies(nestedPair -> {
                assertThat(nestedPair.getLeft()).isInstanceOf(Integer.class);
                assertThat(nestedPair.getRight()).isInstanceOf(String.class);
            });
        });
    }
}
