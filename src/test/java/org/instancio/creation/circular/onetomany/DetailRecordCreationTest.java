package org.instancio.creation.circular.onetomany;

import org.instancio.pojo.circular.onetomany.DetailRecord;
import org.instancio.testsupport.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
public class DetailRecordCreationTest extends CreationTestTemplate<DetailRecord> {

    @Override
    @AutoVerificationDisabled
    protected void verify(DetailRecord result) {
        assertThat(result.getMainRecord()).isNotNull();
        assertThat(result.getMainRecord().getDetailRecords())
                .as("Should have an empty collection to break the cycle")
                .isEmpty();
    }

}
