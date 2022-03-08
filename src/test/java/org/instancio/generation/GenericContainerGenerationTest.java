package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class GenericContainerGenerationTest {

    @Test
    void generate() {
        GenericContainer<?> result = Instancio.of(GenericContainer.class)
                .withType(String.class)
                .create();

        assertThat(result.getValue()).isInstanceOf(String.class);
        assertThat(result.getList()).isNotEmpty().hasOnlyElementsOfType(String.class);
        assertThat(result.getArray()).isNotEmpty().hasOnlyElementsOfType(String.class);
    }
}
