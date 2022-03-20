package org.instancio.creation.cyclic.onetomany;

import org.instancio.pojo.cyclic.onetomany.DetailRecord;
import org.instancio.pojo.cyclic.onetomany.MainRecord;
import org.instancio.testsupport.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
@AutoVerificationDisabled
public class DetailRecordCreationTest extends CreationTestTemplate<DetailRecord> {

    @Override
    protected void verify(DetailRecord result) {
        final MainRecord mainRecord1 = result.getMainRecord();
        assertThat(mainRecord1).isNotNull();

        assertThat(mainRecord1.getDetailRecords()).isNotEmpty()
                .allSatisfy(detail -> {
                    final MainRecord innerMainRecord = detail.getMainRecord();
                    assertThat(innerMainRecord.getDetailRecords())
                            .as("Should have an empty collection to break the cycle")
                            .isEmpty();
                });
    }

}
