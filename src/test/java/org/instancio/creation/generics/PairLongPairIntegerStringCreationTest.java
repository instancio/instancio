package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.PairLongPairIntegerString;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairLongPairIntegerStringCreationTest extends CreationTestTemplate<PairLongPairIntegerString> {

    @Override
    protected void verify(PairLongPairIntegerString result) {
        assertThat(result.getPairLongPairIntegerString()).isInstanceOf(Pair.class);
        assertThat(result.getPairLongPairIntegerString().getLeft()).isInstanceOf(Long.class);

        final Pair<Integer, String> nestedPair = result.getPairLongPairIntegerString().getRight();

        assertThat(nestedPair).satisfies(pair -> {
            assertThat(pair.getLeft()).isInstanceOf(Integer.class);
            assertThat(pair.getRight()).isInstanceOf(String.class);
        });
    }
}
