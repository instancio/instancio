package org.instancio.creation.collections.lists;

import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListLocalDateTimeTest extends CreationTestTemplate<List<LocalDateTime>> {

    @Override
    protected void verify(List<LocalDateTime> result) {
        assertThat(result)
                .isNotEmpty()
                .hasOnlyElementsOfType(LocalDateTime.class);
    }
}
