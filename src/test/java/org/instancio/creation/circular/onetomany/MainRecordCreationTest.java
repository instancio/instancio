package org.instancio.creation.circular.onetomany;

import org.instancio.pojo.circular.onetomany.DetailRecord;
import org.instancio.pojo.circular.onetomany.MainRecord;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.CircularRefsTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CircularRefsTag
public class MainRecordCreationTest extends CreationTestTemplate<MainRecord> {

    @Override
    @AutoVerificationDisabled
    protected void verify(MainRecord result) {
        assertThat(result.getDetailRecords())
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(DetailRecord.class)
                .allSatisfy(detail ->
                        assertThat(detail.getMainRecord())
                                .as("Null value to break the cycle")
                                .isNull());
    }

}
