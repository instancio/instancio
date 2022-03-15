package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.ConcurrentMapIntegerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrentMapIntegerStringCreationTest extends CreationTestTemplate<ConcurrentMapIntegerString> {

    @Override
    protected void verify(ConcurrentMapIntegerString result) {
        assertThat(result.getMap()).hasSize(Constants.COLLECTION_SIZE);
    }

}
