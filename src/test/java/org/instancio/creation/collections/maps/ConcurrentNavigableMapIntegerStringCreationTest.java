package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.ConcurrentNavigableMapIntegerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrentNavigableMapIntegerStringCreationTest
        extends CreationTestTemplate<ConcurrentNavigableMapIntegerString> {

    @Override
    protected void verify(ConcurrentNavigableMapIntegerString result) {
        assertThat(result.getMap()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

}
