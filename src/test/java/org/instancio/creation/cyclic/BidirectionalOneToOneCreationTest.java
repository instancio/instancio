package org.instancio.creation.cyclic;

import org.instancio.pojo.cyclic.BidirectionalOneToOne;
import org.instancio.testsupport.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
@AutoVerificationDisabled
public class BidirectionalOneToOneCreationTest extends CreationTestTemplate<BidirectionalOneToOne.Parent> {

    @Override
    protected void verify(BidirectionalOneToOne.Parent result) {
        // Parent -> Child
        assertThat(result.getChild()).isNotNull()
                // Child -> Parent
                .extracting(BidirectionalOneToOne.Child::getParent)
                .satisfies(parent -> assertThat(parent).isNotNull()
                        // Parent -> Child
                        .extracting(BidirectionalOneToOne.Parent::getChild)
                        .satisfies(child -> assertThat(child.getParent()).isNotNull()));
    }

}
