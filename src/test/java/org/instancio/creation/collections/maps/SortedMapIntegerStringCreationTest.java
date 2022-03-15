package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.SortedMapIntegerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class SortedMapIntegerStringCreationTest extends CreationTestTemplate<SortedMapIntegerString> {

    @Override
    protected void verify(SortedMapIntegerString result) {
        assertThat(result.getMap()).hasSize(Constants.COLLECTION_SIZE);
    }

}
