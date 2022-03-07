package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.FooContainer;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class FooContainerGenerationTest {

    @Test
    void generate() {
        FooContainer result = Instancio.of(FooContainer.class).create();

        assertThat(result).isNotNull();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getFooValue()).isInstanceOf(String.class);
        assertThat(result.getItem().getOtherFooValue()).isNotNull();
    }

}
