package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.Settings;
import org.instancio.test.support.java16.record.PhoneRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag(Feature.ASSIGNMENT_TYPE)
@ExtendWith(InstancioExtension.class)
class RecordAssignmentTypeTest {

    @ParameterizedTest
    @EnumSource(AssignmentType.class)
    @DisplayName("Records are created via constructors, therefore no field or method assignment is done")
    void assignmentTypeDoesNotAffectRecords(final AssignmentType assignmentType) {
        final PhoneRecord result = Instancio.of(PhoneRecord.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, assignmentType)
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL)
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL))
                .create();

        assertThat(result.countryCode()).isNotBlank();
        assertThat(result.number()).isNotBlank();
    }
}
