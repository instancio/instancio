package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class GenericItemGenerationTest {

    @Test
    void generate() {
        GenericItem<?> result = Instancio.of(GenericItem.class)
                .withType(String.class)
                .create();

        assertThat(result.getValue()).isInstanceOf(String.class);
    }
}
