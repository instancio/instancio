package org.instancio.creation.circular;

import org.instancio.pojo.circular.CircularList;
import org.instancio.testsupport.tags.CircularRefsTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CircularRefsTag
public class CircularListCreationTest extends CreationTestTemplate<CircularList> {

    @Override
    @AutoVerificationDisabled
    protected void verify(CircularList result) {
        List<CircularList> items = result.getItems();
        assertThat(items).isEmpty();
    }

}
