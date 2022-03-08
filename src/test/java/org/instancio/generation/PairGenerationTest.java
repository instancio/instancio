package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class PairGenerationTest {

    @Test
    void generate() {
        Pair<?, ?> result = Instancio.of(Pair.class).withType(String.class, Integer.class).create();

        assertThat(result).isNotNull();
        assertThat(result.getLeft()).isInstanceOf(String.class);
        assertThat(result.getRight()).isInstanceOf(Integer.class);
    }
}
