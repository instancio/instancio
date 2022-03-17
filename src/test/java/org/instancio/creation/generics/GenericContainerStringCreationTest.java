package org.instancio.creation.generics;

import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class GenericContainerStringCreationTest extends CreationTestTemplate<GenericContainer<String>> {

    @Override
    protected void verify(GenericContainer<String> result) {
        assertThat(result.getValue()).isInstanceOf(String.class);
        assertThat(result.getList()).isNotEmpty().hasOnlyElementsOfType(String.class);
        assertThat(result.getArray()).isNotEmpty().hasOnlyElementsOfType(String.class);
    }
}
