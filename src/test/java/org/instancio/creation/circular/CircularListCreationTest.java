package org.instancio.creation.circular;

import org.instancio.pojo.circular.CircularList;
import org.instancio.testsupport.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
public class CircularListCreationTest extends CreationTestTemplate<CircularList> {

    @Override
    @AutoVerificationDisabled // TODO move this to class level
    protected void verify(CircularList result) {
        List<CircularList> items = result.getItems();
        //assertThat(items).isEmpty(); // TODO
    }

}
