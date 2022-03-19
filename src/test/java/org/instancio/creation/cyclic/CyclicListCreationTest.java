package org.instancio.creation.cyclic;

import org.instancio.pojo.cyclic.CyclicList;
import org.instancio.testsupport.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
@AutoVerificationDisabled
public class CyclicListCreationTest extends CreationTestTemplate<CyclicList> {

    @Override
    protected void verify(CyclicList result) {
        List<CyclicList> items = result.getItems();
        assertThat(items)
                .isNotEmpty()
                .allSatisfy(item -> assertThat(item.getItems())
                        .isNotEmpty()
                        .allSatisfy(nestedItem -> assertThat(nestedItem.getItems()).isNull()));
    }

}
