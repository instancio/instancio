/*
 * Copyright 2022-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.test.jooq;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.jooq.tables.records.JooqPersonRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.setter;
import static org.instancio.test.jooq.InstancioJooq.jooqModel;

@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class JooqTest {

    @RepeatedTest(10)
    void create() {
        final JooqPersonRecord person = Instancio.create(jooqModel(JooqPersonRecord.class));

        assertThat(person.getFirstName()).isNotBlank();
        assertThat(person.getLastName()).isNotBlank();
        assertThat(person.getPreferredName()).isNotBlank();
        assertThat(person.getId()).isPositive();
        assertThat(person.getGender()).isNotBlank().hasSize(1);
        assertThat(person.getDob()).isNotNull();
        assertThat(person.getLastModified()).isNotNull();
    }

    @Test
    void createCustom() {
        final String firstName = "test-name";

        final List<JooqPersonRecord> results = Instancio.ofList(jooqModel(JooqPersonRecord.class))
                .generate(setter(JooqPersonRecord::setDob), gen -> gen.temporal().localDate().past())
                .set(setter(JooqPersonRecord::setFirstName), firstName)
                .assign(Assign
                        .valueOf(setter(JooqPersonRecord::setFirstName))
                        .to(setter(JooqPersonRecord::setPreferredName)))
                .create();

        assertThat(results).isNotEmpty().allSatisfy(person -> {
            assertThat(person.getFirstName()).isEqualTo(firstName);
            assertThat(person.getPreferredName()).isEqualTo(firstName);
            assertThat(person.getDob()).isInThePast();
        });
    }

    @Nested
    class WithCustomSettingsTest {
        private static final int STR_LENGTH = 20;

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.STRING_MIN_LENGTH, STR_LENGTH)
                .set(Keys.STRING_MAX_LENGTH, STR_LENGTH);

        @Test
        void createWithCustomSettings() {
            final JooqPersonRecord person = Instancio.create(jooqModel(JooqPersonRecord.class));

            assertThat(person.getFirstName())
                    .hasSameSizeAs(person.getLastName())
                    .hasSameSizeAs(person.getPreferredName())
                    .hasSize(STR_LENGTH);
        }
    }
}
