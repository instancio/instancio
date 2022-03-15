package org.instancio.creation.circular;

import org.instancio.pojo.circular.BidirectionalOneToOne;
import org.instancio.testsupport.tags.CircularRefsTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CircularRefsTag
public class BidirectionalOneToOneCreationTest extends CreationTestTemplate<BidirectionalOneToOne.Parent> {

    @Override
    @AutoVerificationDisabled
    protected void verify(BidirectionalOneToOne.Parent result) {
        // Parent -> Child
        assertThat(result.getChild()).isNotNull()
                // Child -> Parent
                .extracting(BidirectionalOneToOne.Child::getParent)
                .satisfies(parent -> assertThat(parent)
                        .as("Reached parent again; end cycle with a null value")
                        .isNull());
    }

}
